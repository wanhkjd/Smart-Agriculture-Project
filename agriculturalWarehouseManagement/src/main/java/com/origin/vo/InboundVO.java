package com.origin.vo;

import lombok.Data;

/**
 * 入库单视图对象
 * 供前端入库单列表展示使用
 */
@Data
public class InboundVO {
    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private String category;
    private String batchNo;
    private String supplier;
    private Double quantity;
    private Long locationId;
    private String locationCode;
    private String qualityCheck;
    private String operator;
    private String status;
    private String remark;
    private String createdAt;
}
