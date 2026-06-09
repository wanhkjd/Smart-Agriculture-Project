package com.origin.pattern.strategy;

import com.origin.entity.Inventory;
import java.util.Comparator;
import java.util.List;

/**
 * 最短保质期优先（FEFO）拣货策略 — 保质期最短的优先出库
 */
public class ShortestShelfLifeStrategy implements PickingStrategy {

    @Override
    public String getName() {
        return "FEFO（最短保质期优先）";
    }

    @Override
    public Inventory selectBatch(List<Inventory> batches) {
        return batches.stream()
                .filter(b -> b.getQuantity() > 0 && !"过期".equals(b.getStatus()))
                .min(Comparator.comparing(Inventory::getExpiryDate))
                .orElse(null);
    }

    @Override
    public String generatePickingPath(Inventory batch, List<String> shelfLayout) {
        String locationCode = batch.getLocationCode();
        return locationCode != null ? locationCode : "未知货架";
    }
}
