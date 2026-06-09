package com.origin.pattern.strategy;

import com.origin.entity.WarehouseLocation;
import java.util.List;

/**
 * 策略模式 — 存储策略接口（不同品类农产品存储方式不同）
 */
public interface StorageStrategy {
    String getName();
    WarehouseLocation assignLocation(List<WarehouseLocation> availableLocations);
    String getStorageRule();
}
