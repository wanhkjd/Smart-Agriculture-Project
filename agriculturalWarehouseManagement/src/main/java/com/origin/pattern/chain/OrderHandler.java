package com.origin.pattern.chain;

/**
 * 责任链模式 — 订单处理器抽象类
 */
public abstract class OrderHandler {
    protected OrderHandler next;

    public OrderHandler setNext(OrderHandler next) {
        this.next = next;
        return next;
    }

    public abstract void handle(OrderContext context);

    protected void next(OrderContext context) {
        if (next != null) {
            next.handle(context);
        }
    }
}
