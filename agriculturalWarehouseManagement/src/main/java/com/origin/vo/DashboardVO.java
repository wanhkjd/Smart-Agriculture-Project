package com.origin.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 仪表盘视图对象
 * 汇总首页统计卡片所需的关键指标数据
 */
@Data
@Builder
public class DashboardVO {
    // 商品总数
    private int productCount;
    // 库存记录数
    private int inventoryCount;
    // 临期商品数（7天内过期）
    private int expiringCount;
    // 活跃订单数
    private int orderCount;
    // 待处理补货任务数
    private int pendingReplenishCount;
    // 待质检入库单数
    private int pendingInboundCount;
    // 待处理拣货任务数
    private int pendingPickingCount;
    // 低库存预警数
    private int lowStockCount;
}
