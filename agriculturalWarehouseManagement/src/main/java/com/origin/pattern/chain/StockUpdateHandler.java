package com.origin.pattern.chain;

/**
 * 责任链模式 — 库存更新处理器（链尾）
 */
public class StockUpdateHandler extends OrderHandler {

    @Override
    public void handle(OrderContext context) {
        context.appendLog("正在更新库存");
        // 链尾，不再调用next
    }
}
