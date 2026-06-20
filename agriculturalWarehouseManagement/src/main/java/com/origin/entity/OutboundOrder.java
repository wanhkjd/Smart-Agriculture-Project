package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
  出库单实体类
  映射 outbound_order 表，记录农产品出库拣货、路径规划、库存扣减
   */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundOrder {
      // 出库单ID   
    private Long id;
      // 出库单号   
    private String orderNo;
      // 关联商品ID   
    private Long productId;
      // 批次号   
    private String batchNo;
      // 出库数量
    private Double quantity;
      // 拣货策略：FIFO/FEFO/CATEGORY
    private String pickingStrategy;
      // 拣货路径
    private String pickingPath;
      // 操作员   
    private String operator;
      // 状态：待拣货/拣货中/已完成/已取消   
    private String status;
      // 关联客户订单ID   
    private Long customerOrderId;
      // 创建时间   
    private String createdAt;
      // 更新时间   
    private String updatedAt;

      // 联表查询字段 — 商品名称   
    private String productName;
      // 联表查询字段 — 商品品类   
    private String category;
}
