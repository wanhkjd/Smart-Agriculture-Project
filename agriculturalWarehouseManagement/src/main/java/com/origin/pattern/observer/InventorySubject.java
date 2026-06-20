package com.origin.pattern.observer;

import com.origin.entity.Inventory;
import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式 — 库存被观察者（库存中心）
 */
public class InventorySubject {
    private final List<InventoryObserver> observers = new ArrayList<>();

    public void attach(InventoryObserver observer) {
        observers.add(observer);
    }

    public void detach(InventoryObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String event, Inventory inventory) {
        for (InventoryObserver observer : observers) {
            observer.onInventoryChange(event, inventory);
        }
    }
}
