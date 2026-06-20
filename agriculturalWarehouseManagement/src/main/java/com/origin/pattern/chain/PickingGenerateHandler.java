package com.origin.pattern.chain;

/**
 * 责任链模式 — 生成拣货单处理器
 */
public class PickingGenerateHandler extends OrderHandler {

    @Override
    public void handle(OrderContext context) {
        context.appendLog("正在生成拣货单");
        context.setPickingGenerated(true);
        next(context);
    }
}
