package com.origin.service.impl;

import com.origin.dto.OrderProcessDTO;
import com.origin.entity.CustomerOrder;
import com.origin.entity.Inventory;
import com.origin.entity.OperationLog;
import com.origin.entity.OutboundOrder;
import com.origin.entity.PickingTask;
import com.origin.exception.BusinessException;
import com.origin.mapper.*;
import com.origin.pattern.factory.DocumentFactory;
import com.origin.pattern.strategy.FifoPickingStrategy;
import com.origin.pattern.singleton.LogManager;
import com.origin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单服务实现
 * 集成责任链模式处理订单流水线（库存锁定→生成拣货单→打印物流单→更新库存）
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CustomerOrderMapper orderMapper;
    private final OutboundOrderMapper outboundMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final PickingTaskMapper pickingTaskMapper;
    private final OperationLogMapper logMapper;

    /**
     * 查询所有客户订单（联表商品）
     * */
    @Override
    public List<CustomerOrder> findAll() {
        return orderMapper.findAllWithProduct();
    }

    /**
     * 根据ID查询客户订单
     * */
    @Override
    public CustomerOrder findById(Long id) {
        CustomerOrder o = orderMapper.findByIdWithProduct(id);
        if (o == null) throw new BusinessException("订单不存在");
        return o;
    }

    /**
     * 创建客户订单
     * */
    @Override
    @Transactional
    public CustomerOrder create(OrderProcessDTO dto) {
        if (productMapper.findById(dto.getProductId()) == null) {
            throw new BusinessException("商品不存在");
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BusinessException("订单数量必须大于0");
        }
        Double totalStock = inventoryMapper.getTotalStockByProductId(dto.getProductId());
        if (totalStock == null || totalStock <= 0) {
            throw new BusinessException("该商品当前无库存");
        }
        if (dto.getQuantity() > totalStock) {
            throw new BusinessException("订单数量(" + dto.getQuantity() + ")超过库存总量(" + totalStock + ")");
        }

        CustomerOrder order = CustomerOrder.builder()
                .orderNo(DocumentFactory.generateDocNo("ORDER"))
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .customerName(dto.getCustomerName())
                .customerPhone(dto.getCustomerPhone())
                .deliveryAddress(dto.getDeliveryAddress())
                .status("待处理")
                .build();
        orderMapper.insert(order);

        logMapper.insert(OperationLog.builder()
                .operation("创建订单").targetType("customer_order").targetId(order.getId())
                .operator(dto.getCustomerName()).detail("订单号:" + order.getOrderNo()).build());

        return findById(order.getId());
    }

    /**
     * 处理订单：库存锁定 → 生成出库单 → 生成拣货任务 → 打印物流单
     * 后续出库拣货流程与普通出库单一致，最终完成时锁定库存自动释放
     */
    @Override
    @Transactional
    public CustomerOrder processOrder(Long id) {
        CustomerOrder order = findById(id);
        if (!"待处理".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许处理");
        }

        // 校验库存
        List<Inventory> batches = inventoryMapper.findByProductIdWithProduct(order.getProductId());
        if (batches.isEmpty()) {
            throw new BusinessException("该商品当前无库存，无法处理订单");
        }
        double totalStock = batches.stream().mapToDouble(Inventory::getQuantity).sum();
        if (order.getQuantity() > totalStock) {
            throw new BusinessException("订单数量(" + order.getQuantity() + ")超过库存总量(" + totalStock + ")，无法处理");
        }

        // 1. 库存锁定：FIFO 选择合适的批次并锁定
        Inventory selectedBatch = new FifoPickingStrategy().selectBatch(batches);
        if (selectedBatch == null) {
            throw new BusinessException("无法匹配合适的库存批次");
        }
        inventoryMapper.updateStatus(selectedBatch.getId(), "锁定");
        order.setStatus("已锁定库存");
        orderMapper.updateStatus(order.getId(), "已锁定库存");

        String locationCode = selectedBatch.getLocationCode();
        if (locationCode == null) locationCode = "未知货架";
        String pickingPath = locationCode;
        OutboundOrder outbound = OutboundOrder.builder()
                .orderNo(DocumentFactory.generateDocNo("OUTBOUND"))
                .productId(order.getProductId())
                .batchNo(selectedBatch.getBatchNo())
                .quantity(order.getQuantity())
                .pickingPath(pickingPath)
                .operator(order.getCustomerName())
                .status("待拣货")
                .customerOrderId(order.getId())
                .build();
        outboundMapper.insert(outbound);

        // 3. 生成拣货任务（同步拣货路径）
        PickingTask task = PickingTask.builder()
                .taskNo(DocumentFactory.generateDocNo("PICKING"))
                .outboundOrderId(outbound.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .pickingPath(pickingPath)
                .status("待分配")
                .build();
        pickingTaskMapper.insert(task);

        // 4. 打印物流单（仅比出库流程多此一步）
        LogManager.getInstance().addLog("物流单打印: 订单" + order.getOrderNo()
                + " 客户:" + order.getCustomerName() + " 地址:" + order.getDeliveryAddress());

        logMapper.insert(OperationLog.builder()
                .operation("订单处理").targetType("customer_order").targetId(id)
                .operator("系统").detail("锁定批次:" + selectedBatch.getBatchNo()
                        + " 出库单:" + outbound.getOrderNo() + " 物流单已打印").build());

        LogManager.getInstance().addLog("订单处理完成: " + order.getOrderNo());
        return order;
    }

    /**
     * 更新客户订单状态，状态变为"已完成"或"已取消"时释放锁定的库存
     * */
    @Override
    public CustomerOrder updateStatus(Long id, String status) {
        CustomerOrder order = findById(id);
        orderMapper.updateStatus(id, status);

        if ("已完成".equals(status) || "已取消".equals(status)) {
            int unlocked = inventoryMapper.unlockByProductId(order.getProductId());
            if (unlocked > 0) {
                logMapper.insert(OperationLog.builder()
                        .operation("解锁库存").targetType("customer_order").targetId(id)
                        .operator("系统").detail("订单" + status + "，释放锁定库存" + unlocked + "条").build());
            }
        }
        return findById(id);
    }

    /**
     * 取消客户订单，同时释放锁定的库存
     * */
    @Override
    public void cancel(Long id) {
        updateStatus(id, "已取消");
    }
}
