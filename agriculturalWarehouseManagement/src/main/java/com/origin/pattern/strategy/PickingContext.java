package com.origin.pattern.strategy;

import com.origin.entity.Inventory;
import java.util.List;

/**
 * 策略模式 — 拣货策略上下文
 */
public class PickingContext {
    private PickingStrategy strategy;

    public PickingContext(PickingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PickingStrategy strategy) {
        this.strategy = strategy;
    }

    public String getStrategyName() {
        return strategy.getName();
    }

    public Inventory selectBatch(List<Inventory> batches) {
        return strategy.selectBatch(batches);
    }

    public String generatePickingPath(Inventory batch, List<String> shelfLayout) {
        return strategy.generatePickingPath(batch, shelfLayout);
    }
}
