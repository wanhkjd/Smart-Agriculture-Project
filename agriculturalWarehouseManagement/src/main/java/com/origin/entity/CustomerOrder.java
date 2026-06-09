package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户订单实体类
 * 映射 customer_order 表，记录客户的农产品订购信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {
    // 订单ID
    private Long id;
    //订单编号
    private String orderNo;
    // 关联商品ID
    private Long productId;
    // 订购数量
    private Double quantity;
    // 客户姓名
    private String customerName;
    // 客户电话
    private String customerPhone;
    // 配送地址
    private String deliveryAddress;
    //状态：待处理/已锁定库存/拣货中/已发货/已取消
    private String status;
    // 创建时间
    private String createdAt;
    //更新时间
    private String updatedAt;

    //联表查询字段 — 商品名称
    private String productName;
    // 联表查询字段 — 商品品类
    private String category;
}
