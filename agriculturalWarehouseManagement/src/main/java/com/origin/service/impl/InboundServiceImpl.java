package com.origin.service.impl;

import com.origin.dto.InboundCreateDTO;
import com.origin.entity.*;
import com.origin.exception.BusinessException;
import com.origin.mapper.*;
import com.origin.pattern.command.CommandInvoker;
import com.origin.pattern.command.InboundCommand;
import com.origin.pattern.factory.DocumentFactory;
import com.origin.pattern.factory.WarehouseDocument;
import com.origin.pattern.singleton.LogManager;
import com.origin.pattern.strategy.ColdChainStorageStrategy;
import com.origin.pattern.strategy.NormalStorageStrategy;
import com.origin.pattern.strategy.StorageContext;
import com.origin.service.InboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 入库服务实现
 * 集成工厂方法模式（单据创建）、策略模式（存储分配）、命令模式（操作撤销）
 */
@Service
@RequiredArgsConstructor
public class InboundServiceImpl implements InboundService {
    private final InboundOrderMapper inboundMapper;
    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final WarehouseLocationMapper locationMapper;
    private final OperationLogMapper logMapper;

    /**
     * 命令模式：命令调用者，支持操作撤销
     * */
    private final CommandInvoker commandInvoker = new CommandInvoker();

    /**
     * 查询所有入库单（联表商品和货位信息）
     * */
    @Override
    public List<InboundOrder> findAll() {
        return inboundMapper.findAllWithProduct();
    }

    /**
     * 根据ID查询入库单
     * */
    @Override
    public InboundOrder findById(Long id) {
        InboundOrder o = inboundMapper.findByIdWithProduct(id);
        if (o == null) throw new BusinessException("入库单不存在");
        return o;
    }

    /**
     * 创建入库单
     * 使用工厂方法创建入库单据，策略模式根据存储条件分配货位
     */
    @Override
    @Transactional
    public InboundOrder create(InboundCreateDTO dto) {
        Product product = productMapper.findById(dto.getProductId());
        if (product == null) throw new BusinessException("商品不存在");

        // 工厂方法模式：创建入库单据
        WarehouseDocument doc = DocumentFactory.createDocument("INBOUND", dto.getProductId(), dto.getQuantity());
        LogManager.getInstance().addLog("创建" + doc.getDescription());

        WarehouseLocation location = null;
        if (dto.getLocationId() != null) {
            location = locationMapper.findById(dto.getLocationId());
            if (location == null) throw new BusinessException("所选货位不存在");
        } else {
            // 策略模式：根据存储条件选择存储策略自动分配货位
            StorageContext storageCtx = getStorageContext(product.getStorageCondition());
            List<WarehouseLocation> locations = locationMapper.findAll();
            location = storageCtx.assignLocation(locations);
        }

        String batchNo = (dto.getBatchNo() != null && !dto.getBatchNo().isEmpty()) ? dto.getBatchNo() : doc.getDocNo();

        String productionDate = (dto.getProductionDate() != null && !dto.getProductionDate().isEmpty())
                ? dto.getProductionDate() : null;

        InboundOrder order = InboundOrder.builder()
                .orderNo(doc.getDocNo())
                .productId(dto.getProductId())
                .batchNo(batchNo)
                .supplier(dto.getSupplier())
                .quantity(dto.getQuantity())
                .locationId(location != null ? location.getId() : null)
                .productionDate(productionDate)
                .qualityCheck("待质检")
                .operator(dto.getOperator())
                .status("待质检")
                .remark(dto.getRemark())
                .build();
        inboundMapper.insert(order);

        logMapper.insert(OperationLog.builder()
                .operation("创建入库单").targetType("inbound_order").targetId(order.getId())
                .operator(dto.getOperator()).detail("商品:" + product.getName() + " 数量:" + dto.getQuantity()).build());

        return findById(order.getId());
    }

    /**
     * 质检确认：合格则进入质检中可上架，不合格则取消入库
     * */
    @Override
    @Transactional
    public InboundOrder confirmQuality(Long id, boolean passed, String operator) {
        InboundOrder order = findById(id);
        if (!"待质检".equals(order.getStatus())) throw new BusinessException("当前状态不允许质检操作");

        order.setQualityCheck(passed ? "合格" : "不合格");
        order.setStatus(passed ? "质检中" : "已取消");
        inboundMapper.update(order);

        logMapper.insert(OperationLog.builder()
                .operation("质检确认").targetType("inbound_order").targetId(id)
                .operator(operator).detail(passed ? "质检合格" : "质检不合格，取消入库").build());

        return findById(id);
    }

    /**
     * 上架确认：创建库存记录，使用命令模式封装操作以支持撤销
     */
    @Override
    @Transactional
    public InboundOrder confirmPutaway(Long id, String locationCode, String operator) {
        InboundOrder order = findById(id);
        if (!"质检中".equals(order.getStatus())) throw new BusinessException("当前状态不允许上架操作");

        Product product = productMapper.findById(order.getProductId());
        // 使用入库时填写的生产日期，未填写则默认今天
        String productionDate = order.getProductionDate() != null
                ? order.getProductionDate() : LocalDate.now().toString();
        LocalDate expiryDate = LocalDate.parse(productionDate).plusDays(product.getShelfLifeDays());

        // 若未传入货位编号，从入库单关联的货位获取
        if (locationCode == null || locationCode.isEmpty()) {
            WarehouseLocation loc = locationMapper.findById(order.getLocationId());
            if (loc != null) locationCode = loc.getCode();
        }

        Inventory inventory = Inventory.builder()
                .productId(order.getProductId())
                .batchNo(order.getBatchNo())
                .quantity(order.getQuantity())
                .locationId(order.getLocationId())
                .shelfNo(locationCode)
                .productionDate(productionDate)
                .expiryDate(expiryDate.toString())
                .status("正常")
                .build();
        inventoryMapper.insert(inventory);

        // 命令模式：封装入库操作，支持撤销
        InboundCommand cmd = new InboundCommand(order.getId(), inventory.getId(),
                order.getQuantity(), operator, inboundMapper, inventoryMapper, logMapper);
        commandInvoker.execute(cmd);

        LogManager.getInstance().addLog("入库上架: 订单" + order.getOrderNo());
        return findById(id);
    }

    /**
     * 取消入库单
     * */
    @Override
    @Transactional
    public void cancel(Long id, String operator) {
        InboundOrder order = findById(id);
        order.setStatus("已取消");
        inboundMapper.update(order);
        logMapper.insert(OperationLog.builder()
                .operation("取消入库").targetType("inbound_order").targetId(id)
                .operator(operator).detail("入库单取消").build());
    }

    /**
     * 撤销最近一次入库操作（命令模式）
     * */
    @Override
    public void undoLast() {
        if (commandInvoker.canUndo()) {
            commandInvoker.undoLast();
            LogManager.getInstance().addLog("撤销最近一次入库操作");
        } else {
            throw new BusinessException("没有可撤销的操作");
        }
    }

    /**
     * 检查是否有可撤销的入库操作
     * */
    @Override
    public boolean canUndo() {
        return commandInvoker.canUndo();
    }

    /**
     * 根据存储条件获取对应的存储策略上下文
     * */
    private StorageContext getStorageContext(String storageCondition) {
        if (storageCondition == null) return new StorageContext(new NormalStorageStrategy());
        return switch (storageCondition) {
            case "冷藏", "冷冻" -> new StorageContext(new ColdChainStorageStrategy());
            default -> new StorageContext(new NormalStorageStrategy());
        };
    }
}
