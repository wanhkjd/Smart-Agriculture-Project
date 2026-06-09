package com.origin.controller;

import com.origin.common.Result;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.ReportService;
import com.origin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 报表与统计 Controller
 * 提供仪表盘、库存周转率报表、操作日志查询 REST API
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final PermissionProxy permissionProxy;

    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        return Result.success(reportService.getDashboard());
    }

    /**
     * 获取库存周转率报表（传page>0时启用分页）
     */
    @GetMapping("/inventory-turnover")
    public Result<?> inventoryTurnover(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize) {
        var data = reportService.getInventoryTurnover();
        return Result.success(PageUtils.paginate(data, page, pageSize));
    }

    /**
     * 获取图表数据（商品数量分布 + 分类占比）
     */
    @GetMapping("/chart-data")
    public Result<?> chartData() {
        return Result.success(reportService.getChartData());
    }

    /**
     * 获取操作日志（传page>0时启用分页）
     */
    @GetMapping("/operation-logs")
    public Result<?> operationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        permissionProxy.check("查看报表");
        var data = reportService.getOperationLogs();
        return Result.success(PageUtils.paginate(data, page, pageSize));
    }
}
