package com.origin.pattern.strategy;

import com.origin.entity.Inventory;
import java.util.Comparator;
import java.util.List;

/**
 * 先入先出（FIFO）拣货策略 — 按入库时间最早优先
 */
public class FifoPickingStrategy implements PickingStrategy {

    @Override
    public String getName() {
        return "FIFO（先入先出）";
    }

    @Override
    public Inventory selectBatch(List<Inventory> batches) {
        return batches.stream()
                .filter(b -> b.getQuantity() > 0 && !"过期".equals(b.getStatus()))
                .min(Comparator.comparing(Inventory::getCreatedAt))
                .orElse(null);
    }

    @Override
    public String generatePickingPath(Inventory batch, List<String> shelfLayout) {
        String locationCode = batch.getLocationCode();
        return locationCode != null ? locationCode : "未知货架";
    }
}
