package com.origin.pattern.strategy;

import com.origin.entity.WarehouseLocation;
import java.util.List;

/**
 * 常温存储策略 — 适用于叶菜、根茎、水果类
 */
public class NormalStorageStrategy implements StorageStrategy {

    @Override
    public String getName() {
        return "常温存储";
    }

    @Override
    public WarehouseLocation assignLocation(List<WarehouseLocation> availableLocations) {
        return availableLocations.stream()
                .filter(l -> "常温区".equals(l.getZone()) && "空闲".equals(l.getStatus()))
                .findFirst()
                .orElseGet(() -> availableLocations.stream()
                        .filter(l -> "空闲".equals(l.getStatus()))
                        .findFirst().orElse(null));
    }

    @Override
    public String getStorageRule() {
        return "常温区货架，先入先出，注意通风";
    }
}
