package com.origin.vo;

import lombok.Data;

/**
 * 出库单视图对象
 * 供前端出库单列表展示使用
 */
@Data
public class OutboundVO {
    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private String category;
    private String batchNo;
    private Double quantity;
    private String pickingStrategy;
    private String pickingPath;
    private String operator;
    private String status;
    private Long customerOrderId;
    private String createdAt;
}
