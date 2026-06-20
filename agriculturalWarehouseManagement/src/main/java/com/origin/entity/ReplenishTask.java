package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 补货任务实体类
 * 映射 replenish_task 表，记录低库存/临期商品的补货申请与执行
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplenishTask {
      // 任务ID  
    private Long id;
      // 任务编号  
    private String taskNo;
      // 关联商品ID  
    private Long productId;
      // 当前库存数量  
    private Double currentQuantity;
      // 补货数量  
    private Double replenishQuantity;
      // 补货原因：低库存/过期/报损  
    private String reason;
      // 创建人  
    private String creator;
      // 状态：待处理/补货中/已完成/已取消  
    private String status;
      // 创建时间  
    private String createdAt;
      // 更新时间  
    private String updatedAt;

      // 联表查询字段 — 商品名称  
    private String productName;
      // 联表查询字段 — 商品品类  
    private String category;
}
