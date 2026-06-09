package com.origin.pattern.factory;

/**
 * 工厂方法模式 — 出库单具体产品
 */
public class OutboundDocument extends WarehouseDocument {

    public OutboundDocument(String docNo, Long productId, Double quantity) {
        super(docNo, "出库单", productId, quantity);
    }

    @Override
    public String getDescription() {
        return String.format("出库单[%s] - 商品ID:%d, 数量:%.2fkg", docNo, productId, quantity);
    }
}
