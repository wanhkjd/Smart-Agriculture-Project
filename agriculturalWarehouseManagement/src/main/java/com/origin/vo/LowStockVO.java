package com.origin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 低库存预警 VO
 * 按商品维度汇总库存，判断是否需要补货预警
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockVO {
    private Long productId;
    private String productName;
    private String category;
    private Double totalQuantity;
    private String storageCondition;
    private String unit;
}