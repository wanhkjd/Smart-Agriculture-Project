package com.origin.pattern.observer;

import com.origin.entity.Inventory;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 观察者模式 — 预警观察者（库存变化时检查临期/低库存）
 */
public class WarningObserver implements InventoryObserver {

    @Override
    public void onInventoryChange(String event, Inventory inventory) {
        if (inventory == null) return;

        StringBuilder warning = new StringBuilder();

        // 检查临期
        try {
            LocalDate expiry = LocalDate.parse(inventory.getExpiryDate());
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
            if (daysLeft <= 7 && daysLeft > 0) {
                warning.append(String.format("[临期预警] %s 批次%s 距过期还有%d天",
                        inventory.getProductName(), inventory.getBatchNo(), daysLeft));
            } else if (daysLeft <= 0) {
                warning.append(String.format("[过期预警] %s 批次%s 已过期!",
                        inventory.getProductName(), inventory.getBatchNo()));
            }
        } catch (Exception ignored) {}

        // 检查低库存
        if (inventory.getQuantity() != null && inventory.getQuantity() <= 10) {
            if (!warning.isEmpty()) warning.append(" | ");
            warning.append(String.format("[低库存预警] %s 当前库存: %.2f",
                    inventory.getProductName(), inventory.getQuantity()));
        }

        if (!warning.isEmpty()) {
            System.out.println("==> " + warning);
        }
    }
}
