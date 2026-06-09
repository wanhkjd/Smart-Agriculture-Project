package com.origin.pattern.strategy;

import com.origin.entity.WarehouseLocation;
import java.util.List;

/**
 * 策略模式 — 存储策略上下文
 */
public class StorageContext {
    private StorageStrategy strategy;

    public StorageContext(StorageStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(StorageStrategy strategy) {
        this.strategy = strategy;
    }

    public String getStrategyName() {
        return strategy.getName();
    }

    public WarehouseLocation assignLocation(List<WarehouseLocation> availableLocations) {
        return strategy.assignLocation(availableLocations);
    }

    public String getStorageRule() {
        return strategy.getStorageRule();
    }
}
