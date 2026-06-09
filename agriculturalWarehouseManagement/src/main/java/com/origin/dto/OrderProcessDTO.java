package com.origin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户订单创建请求 DTO
 * 接收前端下单的表单数据
 */
@Data
public class OrderProcessDTO {
    // 商品ID
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    // 订购数量
    @NotNull(message = "数量不能为空")
    private Double quantity;
    // 客户名称
    @NotBlank(message = "客户名称不能为空")
    private String customerName;
    // 客户电话
    private String customerPhone;
    // 配送地址
    private String deliveryAddress;
}
