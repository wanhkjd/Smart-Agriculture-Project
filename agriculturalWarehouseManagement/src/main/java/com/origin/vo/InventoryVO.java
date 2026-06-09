package com.origin.vo;

import lombok.Data;

/**
 * 库存视图对象
 * 供前端库存列表展示使用，含距到期天数
 */
@Data
public class InventoryVO {
    private Long id;
    private Long productId;
    private String productName;
    private String category;
    private String batchNo;
    private Double quantity;
    private Long locationId;
    private String locationCode;
    private String shelfNo;
    private String productionDate;
    private String expiryDate;
    private String status;
    private String storageCondition;
    /** 距到期天数（计算字段） */
    private long daysUntilExpiry;
}
