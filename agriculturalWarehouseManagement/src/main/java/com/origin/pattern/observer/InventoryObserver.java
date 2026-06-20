package com.origin.pattern.observer;

import com.origin.entity.Inventory;

/**
 * 观察者模式 — 库存变化观察者接口
 */
public interface InventoryObserver {
    void onInventoryChange(String event, Inventory inventory);
}
