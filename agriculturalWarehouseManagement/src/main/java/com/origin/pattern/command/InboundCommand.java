package com.origin.pattern.command;

import com.origin.mapper.InboundOrderMapper;
import com.origin.mapper.InventoryMapper;
import com.origin.mapper.OperationLogMapper;
import com.origin.entity.OperationLog;

/**
 * 命令模式 — 入库操作命令
 */
public class InboundCommand implements WarehouseCommand {
    private final Long inboundId;
    private final Long inventoryId;
    private final Double quantity;
    private final String operator;
    private final InboundOrderMapper inboundMapper;
    private final InventoryMapper inventoryMapper;
    private final OperationLogMapper logMapper;
    private boolean executed = false;

    public InboundCommand(Long inboundId, Long inventoryId, Double quantity, String operator,
                          InboundOrderMapper inboundMapper, InventoryMapper inventoryMapper,
                          OperationLogMapper logMapper) {
        this.inboundId = inboundId;
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.operator = operator;
        this.inboundMapper = inboundMapper;
        this.inventoryMapper = inventoryMapper;
        this.logMapper = logMapper;
    }

    @Override
    public void execute() {
        inboundMapper.updateStatus(inboundId, "已上架");
        executed = true;
        logMapper.insert(OperationLog.builder()
                .operation("入库确认").targetType("inbound_order").targetId(inboundId)
                .operator(operator).detail("入库数量: " + quantity).build());
    }

    @Override
    public void undo() {
        if (executed) {
            inboundMapper.updateStatus(inboundId, "质检中");
            inventoryMapper.deleteById(inventoryId);
            logMapper.insert(OperationLog.builder()
                    .operation("入库撤销").targetType("inbound_order").targetId(inboundId)
                    .operator(operator).detail("撤销入库，删除库存记录#" + inventoryId).build());
        }
    }

    @Override
    public String getDescription() {
        return "入库命令[入库单#" + inboundId + ", 数量:" + quantity + "]";
    }
}
