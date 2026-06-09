package com.origin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 出库单创建请求 DTO
 * 接收前端出库申请的表单数据，含拣货策略选择
 */
@Data
public class OutboundCreateDTO {
    // 商品ID
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    //批次号（由拣货策略自动匹配，无需填写）
    private String batchNo;
    // 出库数量
    @NotNull(message = "数量不能为空")
    private Double quantity;
    //操作员
    @NotBlank(message = "操作员不能为空")
    private String operator;
    //关联客户订单ID（可选）
    private Long customerOrderId;
    //拣货策略：FIFO/FEFO/CATEGORY
    private String pickingStrategy;
}
