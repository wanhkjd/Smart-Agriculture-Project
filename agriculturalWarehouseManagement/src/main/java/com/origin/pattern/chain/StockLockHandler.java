package com.origin.pattern.chain;

/**
 * 责任链模式 — 库存锁定处理器
 */
public class StockLockHandler extends OrderHandler {

    @Override
    public void handle(OrderContext context) {
        context.appendLog("正在锁定库存");
        context.setStockLocked(true);
        next(context);
    }
}
