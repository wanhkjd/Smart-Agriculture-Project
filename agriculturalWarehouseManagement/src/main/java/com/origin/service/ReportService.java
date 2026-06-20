package com.origin.service;

import com.origin.entity.OperationLog;
import com.origin.vo.ChartVO;
import com.origin.vo.DashboardVO;
import com.origin.vo.ReportVO;
import java.util.List;
import java.util.Map;

/**
 * 报表服务接口
 * 提供仪表盘统计、库存报告、操作日志、系统配置和权限校验功能
 */
public interface ReportService {
    /**
     * 获取仪表盘统计数据
     * */
    DashboardVO getDashboard();
    /**
     * 获取库存周转率报表
     * */
    List<ReportVO> getInventoryTurnover();
    /**
     *  获取最近的操作日志
     *  */
    List<OperationLog> getOperationLogs();
    /**
     * 获取系统配置
     * */
    Map<String, String> getConfig();
    /**
     *  更新系统配置项
     *  */
    void updateConfig(String key, String value);
    /**
     * 获取系统运行日志
     * */
    List<String> getSystemLogs();
    /**
     * 检查角色权限
     * */
    String checkPermission(String operation, String role);

    ChartVO getChartData();
}
