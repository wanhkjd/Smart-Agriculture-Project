package com.origin.pattern.strategy;

import com.origin.entity.Inventory;
import java.util.List;

/**
 * 策略模式 — 拣货策略接口
 */
public interface PickingStrategy {
    String getName();
    Inventory selectBatch(List<Inventory> batches);
    String generatePickingPath(Inventory batch, List<String> shelfLayout);
}
