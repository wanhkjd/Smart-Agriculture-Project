package com.origin.service.impl;

import com.origin.entity.OperationLog;
import com.origin.entity.Product;
import com.origin.mapper.*;
import com.origin.pattern.proxy.WarehouseOperationProxy;
import com.origin.pattern.singleton.ConfigManager;
import com.origin.pattern.singleton.LogManager;
import com.origin.service.ReportService;
import com.origin.vo.ChartVO;
import com.origin.vo.DashboardVO;
import com.origin.vo.ReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map;

/**
 * 报表服务实现
 * 集成单例模式（ConfigManager配置、LogManager日志）和代理模式（权限校验）
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final CustomerOrderMapper customerOrderMapper;
    private final ReplenishTaskMapper replenishTaskMapper;
    private final InboundOrderMapper inboundOrderMapper;
    private final OutboundOrderMapper outboundOrderMapper;
    private final PickingTaskMapper pickingTaskMapper;
    private final OperationLogMapper operationLogMapper;

    /**
     * 构建仪表盘统计数据
     * */
    @Override
    public DashboardVO getDashboard() {
        return DashboardVO.builder()
                .productCount(productMapper.findAll().size())
                .inventoryCount(inventoryMapper.countAll())
                .expiringCount(inventoryMapper.countExpiring())
                .orderCount(customerOrderMapper.countActive())
                .pendingReplenishCount(replenishTaskMapper.countPending())
                .pendingInboundCount(inboundOrderMapper.countPending())
                .pendingPickingCount(pickingTaskMapper.countPending())
                .lowStockCount(inventoryMapper.countLowStockProducts())
                .build();
    }

    /**
     * 构建库存周转率报表
     * */
    @Override
    public List<ReportVO> getInventoryTurnover() {
        List<ReportVO> result = new ArrayList<>();
        productMapper.findAll().forEach(p -> {
            Long productId = p.getId();
            double totalInbound = inboundOrderMapper.getTotalInboundByProductId(productId);
            double totalOutbound = outboundOrderMapper.getTotalOutboundByProductId(productId);
            double currentStock = inventoryMapper.getTotalStockByProductId(productId);
            double turnoverRate = currentStock > 0 ? totalOutbound / currentStock : 0;
            int expiringCount = inventoryMapper.countExpiringByProductId(productId);
            int expiredCount = inventoryMapper.countExpiredByProductId(productId);

            result.add(ReportVO.builder()
                    .productName(p.getName())
                    .category(p.getCategory())
                    .currentStock(currentStock)
                    .totalInbound(totalInbound)
                    .totalOutbound(totalOutbound)
                    .turnoverRate(Math.round(turnoverRate * 100.0) / 100.0)
                    .expiringCount(expiringCount)
                    .expiredCount(expiredCount)
                    .build());
        });
        return result;
    }

    /**
     * 获取最近100条操作日志
     * */
    @Override
    public List<OperationLog> getOperationLogs() {
        return operationLogMapper.findRecent();
    }

    /**
     * 获取全局配置（单例 ConfigManager）
     * */
    @Override
    public Map<String, String> getConfig() {
        return ConfigManager.getInstance().getAll();
    }

    /**
     * 更新全局配置项（单例 ConfigManager）
     * */
    @Override
    public void updateConfig(String key, String value) {
        ConfigManager.getInstance().set(key, value);
    }

    /**
     * 获取系统运行日志（单例 LogManager）
     * */
    @Override
    public List<String> getSystemLogs() {
        return LogManager.getInstance().getRecentLogs();
    }

    /**
     *  检查角色权限（代理模式）
     *  */
    @Override
    public String checkPermission(String operation, String role) {
        WarehouseOperationProxy proxy = new WarehouseOperationProxy();
        return proxy.operate(operation, role, "权限验证");
    }

    @Override
    public ChartVO getChartData() {
        List<Product> products = productMapper.findAll();
        List<String> names = new ArrayList<>();
        List<Double> quantities = new ArrayList<>();
        Map<String, Integer> catMap = new LinkedHashMap<>();
        for (Product p : products) {
            names.add(p.getName());
            quantities.add(p.getQuantity() != null ? p.getQuantity() : 0.0);
            String cat = p.getCategory() != null ? p.getCategory() : "未分类";
            catMap.put(cat, catMap.getOrDefault(cat, 0) + 1);
        }
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, Integer> e : catMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", e.getKey());
            item.put("value", e.getValue());
            pieData.add(item);
        }
        return ChartVO.builder()
                .productNames(names)
                .productQuantities(quantities)
                .categoryPie(pieData)
                .build();
    }

}
