package com.origin.pattern.factory;

/**
 * 工厂方法模式 — 补货单具体产品
 */
public class ReplenishDocument extends WarehouseDocument {

    public ReplenishDocument(String docNo, Long productId, Double quantity) {
        super(docNo, "补货单", productId, quantity);
    }

    @Override
    public String getDescription() {
        return String.format("补货单[%s] - 商品ID:%d, 数量:%.2fkg", docNo, productId, quantity);
    }
}
