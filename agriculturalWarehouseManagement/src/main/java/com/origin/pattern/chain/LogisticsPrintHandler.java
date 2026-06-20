package com.origin.pattern.chain;

/**
 * 责任链模式 — 打印物流单处理器
 */
public class LogisticsPrintHandler extends OrderHandler {

    @Override
    public void handle(OrderContext context) {
        context.appendLog("正在打印物流单");
        context.setLogisticsPrinted(true);
        next(context);
    }
}
