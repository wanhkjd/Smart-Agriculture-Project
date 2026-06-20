package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 农产品信息实体类
 * 映射 product 表，记录农产品的名称、类别、保质期、存储条件等信息
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
      // 商品ID   
    private Long id;
      // 商品名称   
    private String name;
      // 品类：叶菜/根茎/水果/冷链品/粮油   
    private String category;
      // 保质期天数   
    private Integer shelfLifeDays;
      // 存储条件：常温/冷藏/冷冻   
    private String storageCondition;
      // 计量单位（kg/L）   
    private String unit;
      // 数量   
    private Double quantity;
      // 商品描述   
    private String description;
      // 状态：1上架 0下架
    private Integer status;
      // 商品图片URL
    private String image;
      // 创建时间
    private String createdAt;
      // 更新时间
    private String updatedAt;
      // 非持久化字段：库存总量
    private Double totalStock;
}
