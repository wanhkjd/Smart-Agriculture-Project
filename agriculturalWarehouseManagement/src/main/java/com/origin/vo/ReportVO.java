package com.origin.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 报表视图对象
 * 供前端库存周转率报表展示使用
 */
@Data
@Builder
public class ReportVO {
    /** 商品名称 */
    private String productName;
    /** 商品品类 */
    private String category;
    /** 累计入库量 */
    private double totalInbound;
    /** 累计出库量 */
    private double totalOutbound;
    /** 当前库存 */
    private double currentStock;
    /** 周转率 */
    private double turnoverRate;
    /** 临期数量 */
    private int expiringCount;
    /** 过期数量 */
    private int expiredCount;
}
