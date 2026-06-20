package com.origin.pattern.command;

import com.origin.entity.Inventory;
import com.origin.mapper.OutboundOrderMapper;
import com.origin.mapper.InventoryMapper;
import com.origin.mapper.OperationLogMapper;
import com.origin.entity.OperationLog;

/**
 * 命令模式 — 出库操作命令
 */
public class OutboundCommand implements WarehouseCommand {
    private final Long outboundId;
    private final Long inventoryId;
    private final Double quantity;
    private final String operator;
    private final OutboundOrderMapper outboundMapper;
    private final InventoryMapper inventoryMapper;
    private final OperationLogMapper logMapper;
    private boolean executed = false;
    private boolean inventoryDeleted = false;

    // 保存库存快照，用于撤销时重建
    private final Long productId;
    private final String batchNo;
    private final Long locationId;
    private final String shelfNo;
    private final String productionDate;
    private final String expiryDate;

    public OutboundCommand(Long outboundId, Inventory inventory, Double quantity, String operator,
                           OutboundOrderMapper outboundMapper, InventoryMapper inventoryMapper,
                           OperationLogMapper logMapper) {
        this.outboundId = outboundId;
        this.inventoryId = inventory.getId();
        this.quantity = quantity;
        this.operator = operator;
        this.outboundMapper = outboundMapper;
        this.inventoryMapper = inventoryMapper;
        this.logMapper = logMapper;
        this.productId = inventory.getProductId();
        this.batchNo = inventory.getBatchNo();
        this.locationId = inventory.getLocationId();
        this.shelfNo = inventory.getShelfNo();
        this.productionDate = inventory.getProductionDate();
        this.expiryDate = inventory.getExpiryDate();
    }

    @Override
    public void execute() {
        outboundMapper.updateStatus(outboundId, "已完成");
        inventoryMapper.updateQuantity(inventoryId, -quantity);
        executed = true;

        // 出库后库存为0则删除记录
        Double remaining = inventoryMapper.getQuantityById(inventoryId);
        if (remaining != null && remaining <= 0) {
            inventoryMapper.deleteById(inventoryId);
            inventoryDeleted = true;
        }

        logMapper.insert(OperationLog.builder()
                .operation("出库确认").targetType("outbound_order").targetId(outboundId)
                .operator(operator).detail("出库数量: " + quantity).build());
    }

    @Override
    public void undo() {
        if (executed) {
            outboundMapper.updateStatus(outboundId, "已拣货");

            if (inventoryDeleted) {
                // 库存记录已被删除，需要重建
                Inventory restored = Inventory.builder()
                        .id(inventoryId)
                        .productId(productId)
                        .batchNo(batchNo)
                        .quantity(quantity)
                        .locationId(locationId)
                        .shelfNo(shelfNo)
                        .productionDate(productionDate)
                        .expiryDate(expiryDate)
                        .status("正常")
                        .build();
                inventoryMapper.insert(restored);
            } else {
                inventoryMapper.updateQuantity(inventoryId, quantity);
            }

            logMapper.insert(OperationLog.builder()
                    .operation("出库撤销").targetType("outbound_order").targetId(outboundId)
                    .operator(operator).detail("撤销出库数量: " + quantity).build());
        }
    }

    @Override
    public String getDescription() {
        return "出库命令[出库单#" + outboundId + ", 数量:" + quantity + "]";
    }
}
