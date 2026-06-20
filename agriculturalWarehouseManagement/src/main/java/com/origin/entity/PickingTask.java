package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

  /**
 * 拣货任务实体类
 * 映射 picking_task 表，记录拣货员的拣货任务分配与执行状态
   */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickingTask {
      // 任务ID   
    private Long id;
      // 任务编号   
    private String taskNo;
      // 关联出库单ID   
    private Long outboundOrderId;
      // 关联商品ID   
    private Long productId;
      // 拣货数量   
    private Double quantity;
      // 拣货员   
    private String picker;
      // 拣货路径   
    private String pickingPath;
      // 状态：待分配/已分配/拣货中/已完成   
    private String status;
      // 创建时间   
    private String createdAt;
      // 更新时间   
    private String updatedAt;

      // 联表查询字段 — 商品名称   
    private String productName;
      // 联表查询字段 — 批次号   
    private String batchNo;
}
