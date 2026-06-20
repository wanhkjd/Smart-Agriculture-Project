package com.origin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 补货任务创建请求 DTO
 * 接收前端补货申请的表单数据
 */
@Data
public class ReplenishCreateDTO {
    // 商品ID
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    // 补货数量
    @NotNull(message = "补货数量不能为空")
    private Double replenishQuantity;
    // 补货原因（低库存/过期/报损）
    @NotBlank(message = "补货原因不能为空")
    private String reason;
    //创建人
    @NotBlank(message = "创建人不能为空")
    private String creator;
}
