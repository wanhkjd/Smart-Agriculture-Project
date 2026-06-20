package com.origin.pattern.factory;

/**
 * 工厂方法模式 — 拣货单具体产品
 */
public class PickingDocument extends WarehouseDocument {

    public PickingDocument(String docNo, Long productId, Double quantity) {
        super(docNo, "拣货单", productId, quantity);
    }

    @Override
    public String getDescription() {
        return String.format("拣货单[%s] - 商品ID:%d, 数量:%.2fkg", docNo, productId, quantity);
    }
}
