package com.origin.pattern.factory;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 工厂方法模式 — 仓库单据抽象产品
 */
@Data
@AllArgsConstructor
public abstract class WarehouseDocument {
    protected String docNo;
    protected String docType;
    protected Long productId;
    protected Double quantity;

    public abstract String getDescription();
}
