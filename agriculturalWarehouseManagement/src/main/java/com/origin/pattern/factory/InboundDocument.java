package com.origin.pattern.factory;

/**
 * 工厂方法模式 — 入库单具体产品
 */
public class InboundDocument extends WarehouseDocument {

    public InboundDocument(String docNo, Long productId, Double quantity) {
        super(docNo, "入库单", productId, quantity);
    }

    @Override
    public String getDescription() {
        return String.format("入库单[%s] - 商品ID:%d, 数量:%.2fkg", docNo, productId, quantity);
    }
}
