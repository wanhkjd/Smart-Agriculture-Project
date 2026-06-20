package com.origin.pattern.strategy;

import com.origin.entity.WarehouseLocation;
import java.util.List;

/**
 * 冷链存储策略 — 适用于冷链品
 */
public class ColdChainStorageStrategy implements StorageStrategy {

    @Override
    public String getName() {
        return "冷链存储";
    }

    @Override
    public WarehouseLocation assignLocation(List<WarehouseLocation> availableLocations) {
        return availableLocations.stream()
                .filter(l -> ("冷藏区".equals(l.getZone()) || "冷冻区".equals(l.getZone()))
                        && "空闲".equals(l.getStatus()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getStorageRule() {
        return "温度带优先，批次管理严格，先进先出+保质期最短优先";
    }
}
