package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 入库单实体类
 * 映射 inbound_order 表，记录农产品入库申请、质检、上架全流程
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InboundOrder {
    // 入库单ID
    private Long id;
    // 入库单号
    private String orderNo;
    // 关联商品ID
    private Long productId;
    // 批次号
    private String batchNo;
    // 供应商
    private String supplier;
    // 入库数量
    private Double quantity;
    // 关联货位ID
    private Long locationId;
    // 生产日期
    private String productionDate;
    // 质检结果：待质检/合格/不合格
    private String qualityCheck;
    // 操作员
    private String operator;
    // 状态：待质检/质检中/已上架/已取消
    private String status;
    // 备注
    private String remark;
    // 创建时间
    private String createdAt;
    // 更新时间
    private String updatedAt;


    // 联表查询字段 — 商品名称
    private String productName;
    // 联表查询字段 — 商品品类
    private String category;
    // 联表查询字段 — 货位编码
    private String locationCode;
}
