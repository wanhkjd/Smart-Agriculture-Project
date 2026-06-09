package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存台账实体类
 * 映射 inventory 表，记录每批次商品的数量、货位、生产日期、保质期等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
     // 库存记录ID
    private Long id;
     // 关联商品ID
    private Long productId;
     // 批次号
    private String batchNo;
     // 库存数量
    private Double quantity;
     // 关联货位ID
    private Long locationId;
     // 货架编号
    private String shelfNo;
     // 生产日期
    private String productionDate;
     // 过期日期
    private String expiryDate;
     // 状态：正常/临期/过期/锁定
    private String status;
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
     // 联表查询字段 — 存储条件
    private String storageCondition;
}
