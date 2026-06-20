package com.origin.pattern.strategy;

import com.origin.entity.Inventory;
import java.util.Comparator;
import java.util.List;

/**
 * 分类优先拣货策略 — 叶菜类优先出库
 */
public class CategoryPriorityStrategy implements PickingStrategy {

    @Override
    public String getName() {
        return "分类优先";
    }

    @Override
    public Inventory selectBatch(List<Inventory> batches) {
        List<String> priorityOrder = List.of("叶菜", "水果", "根茎", "冷链品", "粮油");
        return batches.stream()
                .filter(b -> b.getQuantity() > 0 && !"过期".equals(b.getStatus()))
                .min(Comparator.comparingInt(b -> {
                    int idx = priorityOrder.indexOf(b.getCategory());
                    return idx >= 0 ? idx : 99;
                }))
                .orElse(null);
    }

    @Override
    public String generatePickingPath(Inventory batch, List<String> shelfLayout) {
        String locationCode = batch.getLocationCode();
        return locationCode != null ? locationCode : "未知货架";
    }
}
