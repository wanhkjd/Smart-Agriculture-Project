package com.origin.service.impl;

import com.origin.dto.OutboundCreateDTO;
import com.origin.entity.*;
import com.origin.exception.BusinessException;
import com.origin.mapper.*;
import com.origin.pattern.command.CommandInvoker;
import com.origin.pattern.command.OutboundCommand;
import com.origin.pattern.factory.DocumentFactory;
import com.origin.pattern.factory.WarehouseDocument;
import com.origin.pattern.singleton.LogManager;
import com.origin.pattern.strategy.*;
import com.origin.service.OutboundService;
import com.origin.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 出库服务实现
 * 集成工厂方法模式（单据创建）、策略模式（拣货策略）、命令模式（操作撤销）
 */
@Service
@RequiredArgsConstructor
public class OutboundServiceImpl implements OutboundService {
    private final OutboundOrderMapper outboundMapper;
    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final PickingTaskMapper pickingTaskMapper;
    private final CustomerOrderMapper customerOrderMapper;
    private final OperationLogMapper logMapper;

    /**
     * 命令模式：命令调用者，支持操作撤销
     * */
    private final CommandInvoker commandInvoker = new CommandInvoker();
    /**
     * 策略模式：当前拣货策略，默认 FIFO
     * */
    private PickingStrategy currentPickingStrategy = new FifoPickingStrategy();

    /**
     * 查询所有出库单（联表商品信息）
     * */
    @Override
    public List<OutboundOrder> findAll() {
        return outboundMapper.findAllWithProduct();
    }

    /**
     * 根据ID查询出库单
     * */
    @Override
    public OutboundOrder findById(Long id) {
        OutboundOrder o = outboundMapper.findByIdWithProduct(id);
        if (o == null) throw new BusinessException("出库单不存在");
        return o;
    }

    /**
     * 创建出库单：使用工厂方法创建单据，策略模式选择批次和规划拣货路径
     */
    @Override
    @Transactional
    public OutboundOrder create(OutboundCreateDTO dto) {
        Product product = productMapper.findById(dto.getProductId());
        if (product == null) throw new BusinessException("商品不存在");

        // 校验出库数量不能超过库存总量
        Double totalStock = inventoryMapper.getTotalStockByProductId(dto.getProductId());
        if (totalStock == null || totalStock <= 0) throw new BusinessException("该商品当前无库存");
        if (dto.getQuantity() > totalStock) throw new BusinessException("出库数量(" + dto.getQuantity() + ")超过库存总量(" + totalStock + ")");

        WarehouseDocument doc = DocumentFactory.createDocument("OUTBOUND", dto.getProductId(), dto.getQuantity());

        if (dto.getPickingStrategy() != null) {
            setStrategy(dto.getPickingStrategy());
        }

        List<Inventory> batches = inventoryMapper.findByProductIdWithProduct(dto.getProductId());
        List<String> shelfLayout = new ArrayList<>();
        for (Inventory inv : batches) {
            if (inv.getLocationCode() != null) shelfLayout.add(inv.getLocationCode());
        }
        if (shelfLayout.isEmpty()) shelfLayout.add("A-01-01");

        Inventory selected = currentPickingStrategy.selectBatch(batches);
        String shelfCode = selected != null && selected.getLocationCode() != null
                ? selected.getLocationCode() : (shelfLayout.isEmpty() ? "A-01-01" : shelfLayout.get(0));
        String path = shelfCode;

        String batchNo = selected != null ? selected.getBatchNo() :
                (dto.getBatchNo() != null && !dto.getBatchNo().isEmpty() ? dto.getBatchNo() : doc.getDocNo());

        OutboundOrder order = OutboundOrder.builder()
                .orderNo(doc.getDocNo())
                .productId(dto.getProductId())
                .batchNo(batchNo)
                .quantity(dto.getQuantity())
                .pickingStrategy(currentPickingStrategy.getName())
                .pickingPath(path)
                .operator(dto.getOperator())
                .status("待拣货")
                .customerOrderId(dto.getCustomerOrderId())
                .build();
        outboundMapper.insert(order);

        PickingTask task = PickingTask.builder()
                .taskNo(DocumentFactory.generateDocNo("PICKING"))
                .outboundOrderId(order.getId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .pickingPath(path)
                .status("待分配")
                .build();
        pickingTaskMapper.insert(task);

        logMapper.insert(OperationLog.builder()
                .operation("创建出库单").targetType("outbound_order").targetId(order.getId())
                .operator(dto.getOperator()).detail("策略:" + currentPickingStrategy.getName() + " 路径:" + path).build());

        return findById(order.getId());
    }

    /**
     * 移入拣货区：货物从货架移至拣货区，更新拣货路径
     */
    @Override
    @Transactional
    public OutboundOrder moveToPickingArea(Long id, String operator) {
        OutboundOrder order = findById(id);
        if (!"待拣货".equals(order.getStatus())) throw new BusinessException("当前状态不允许移入拣货区");

        order.setStatus("拣货中");
        String newPath = (order.getPickingPath() != null ? order.getPickingPath() : "") + " → 拣货区";
        order.setPickingPath(newPath);
        outboundMapper.update(order);

        // 同步更新拣货任务的路径
        PickingTask task = pickingTaskMapper.findByOutboundOrderId(id);
        if (task != null) {
            task.setPickingPath(newPath);
            pickingTaskMapper.update(task);
        }

        logMapper.insert(OperationLog.builder()
                .operation("移入拣货区").targetType("outbound_order").targetId(id)
                .operator(operator).detail("货物已从货架移至拣货区").build());
        LogManager.getInstance().addLog("移入拣货区: " + order.getOrderNo());
        return findById(id);
    }

    /**
     * 拣货确认：扣减库存，仅管理员或被分配的拣货员/仓管员可操作
     */
    @Override
    @Transactional
    public OutboundOrder confirmPicking(Long id, String operator) {
        OutboundOrder order = findById(id);
        if (!"拣货中".equals(order.getStatus())) throw new BusinessException("请先将货物移入拣货区");

        // 权限校验：只有管理员或被分配的拣货员/仓管员可以确认
        String currentRole = UserContext.getRole();
        String currentRealName = UserContext.getRealName();
        if (currentRole == null) throw new BusinessException("用户信息不存在");
        if (!"管理员".equals(currentRole)) {
            PickingTask task = pickingTaskMapper.findByOutboundOrderId(id);
            if (task == null || task.getPicker() == null ||
                    !task.getPicker().equals(currentRealName)) {
                throw new BusinessException("只有管理员或被分配的拣货员/仓管员可以进行拣货确认");
            }
        }

        List<Inventory> batches = inventoryMapper.findByProductIdWithProduct(order.getProductId());
        if (batches.isEmpty()) throw new BusinessException("该商品当前无库存");

        Inventory target = batches.stream()
                .filter(b -> b.getBatchNo().equals(order.getBatchNo()) && b.getQuantity() >= order.getQuantity())
                .findFirst()
                .orElseGet(() -> batches.stream()
                        .filter(b -> b.getQuantity() >= order.getQuantity())
                        .findFirst()
                        .orElse(null));
        if (target == null) {
            double total = batches.stream().mapToDouble(Inventory::getQuantity).sum();
            if (total < order.getQuantity())
                throw new BusinessException("库存不足：需要" + order.getQuantity() + "，当前库存总量" + total);
            double remaining = order.getQuantity();
            for (Inventory batch : batches) {
                if (remaining <= 0) break;
                if (batch.getQuantity() <= 0) continue;
                double pickQty = Math.min(remaining, batch.getQuantity());
                inventoryMapper.updateQuantity(batch.getId(), -pickQty);
                Double after = inventoryMapper.getQuantityById(batch.getId());
                if (after != null && after <= 0) inventoryMapper.deleteById(batch.getId());
                remaining -= pickQty;
            }
            String finalPath = (order.getPickingPath() != null ? order.getPickingPath() : "") + " → 出口";
            order.setPickingPath(finalPath);
            order.setStatus("已完成");
            outboundMapper.update(order);
            // 同步更新拣货任务状态和路径
            PickingTask task = pickingTaskMapper.findByOutboundOrderId(id);
            if (task != null) {
                task.setPickingPath(finalPath);
                task.setStatus("已完成");
                pickingTaskMapper.update(task);
            }
            if (order.getCustomerOrderId() != null) {
                customerOrderMapper.updateStatus(order.getCustomerOrderId(), "已完成");
                inventoryMapper.unlockByProductId(order.getProductId());
            }
            logMapper.insert(OperationLog.builder()
                    .operation("拣货确认").targetType("outbound_order").targetId(id)
                    .operator(operator).detail("跨批次拣货确认，总数量: " + order.getQuantity()
                            + (order.getCustomerOrderId() != null ? "，关联订单已完成，库存已解锁" : "")).build());
            return findById(id);
        }

        OutboundCommand cmd = new OutboundCommand(order.getId(), target,
                order.getQuantity(), operator, outboundMapper, inventoryMapper, logMapper);
        commandInvoker.execute(cmd);

        // 更新拣货路径和拣货任务状态
        String finalPath = (order.getPickingPath() != null ? order.getPickingPath() : "") + " → 出口";
        order.setPickingPath(finalPath);
        order.setStatus("已完成");
        outboundMapper.update(order);
        PickingTask task = pickingTaskMapper.findByOutboundOrderId(id);
        if (task != null) {
            task.setPickingPath(finalPath);
            task.setStatus("已完成");
            pickingTaskMapper.update(task);
        }
        if (order.getCustomerOrderId() != null) {
            customerOrderMapper.updateStatus(order.getCustomerOrderId(), "已完成");
            inventoryMapper.unlockByProductId(order.getProductId());
        }

        LogManager.getInstance().addLog("拣货确认: " + order.getOrderNo());
        return findById(id);
    }

    /**
     * 取消出库单
     * */
    @Override
    @Transactional
    public void cancel(Long id, String operator) {
        OutboundOrder order = findById(id);
        order.setStatus("已取消");
        outboundMapper.update(order);
        logMapper.insert(OperationLog.builder()
                .operation("取消出库").targetType("outbound_order").targetId(id)
                .operator(operator).detail("出库单取消").build());
    }

    /**
     * 撤销最近一次出库操作（命令模式）
     * */
    @Override
    public void undoLast() {
        if (commandInvoker.canUndo()) {
            commandInvoker.undoLast();
            LogManager.getInstance().addLog("撤销最近一次出库操作");
        } else {
            throw new BusinessException("没有可撤销的操作");
        }
    }

    /**
     * 检查是否有可撤销的出库操作
     * */
    @Override
    public boolean canUndo() {
        return commandInvoker.canUndo();
    }

    /**
     * 切换拣货策略
     * */
    public void setStrategy(String type) {
        currentPickingStrategy = switch (type) {
            case "FIFO" -> new FifoPickingStrategy();
            case "FEFO" -> new ShortestShelfLifeStrategy();
            case "CATEGORY" -> new CategoryPriorityStrategy();
            default -> new FifoPickingStrategy();
        };
    }

    /**
     * 获取当前拣货策略名称
     * */
    public String getStrategyName() {
        return currentPickingStrategy.getName();
    }
}
