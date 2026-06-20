package com.origin.pattern.observer;

import com.origin.entity.Inventory;

/**
 * 观察者模式 — 报表观察者（库存变化时更新报表缓存）
 */
public class ReportObserver implements InventoryObserver {

    @Override
    public void onInventoryChange(String event, Inventory inventory) {
        if (inventory == null) return;
        System.out.printf("==> [报表更新] 事件:%s 商品:%s 批次:%s 数量:%.2f%n",
                event, inventory.getProductName(), inventory.getBatchNo(), inventory.getQuantity());
    }
}
