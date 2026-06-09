package com.origin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 入库单创建请求 DTO
 * 接收前端入库申请的表单数据，带参数校验
 */
@Data
public class InboundCreateDTO {
    // 商品ID
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    //批次号（选填，留空自动生成）
    private String batchNo;
    //入库数量
    @NotNull(message = "数量不能为空")
    private Double quantity;
    // 供应商
    private String supplier;
    //货位ID（可选）
    private Long locationId;
    // 操作员
    @NotBlank(message = "操作员不能为空")
    private String operator;
    // 备注
    private String remark;
    //生产日期
    private String productionDate;
    // 保质期天数
    private Integer shelfLifeDays;
}
