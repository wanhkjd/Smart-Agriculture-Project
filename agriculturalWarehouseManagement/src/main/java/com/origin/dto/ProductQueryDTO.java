package com.origin.dto;

import lombok.Data;

/**
 * 商品查询请求 DTO
 * 支持按品类、关键词、状态筛选商品
 */
@Data
public class ProductQueryDTO {
    //品类筛选
    private String category;
    // 关键词模糊搜索
    private String keyword;
    //状态筛选
    private Integer status;
}
