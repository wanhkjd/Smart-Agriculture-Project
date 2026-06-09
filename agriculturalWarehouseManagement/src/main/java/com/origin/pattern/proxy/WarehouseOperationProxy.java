package com.origin.pattern.proxy;

import java.util.Map;
import java.util.Set;

/**
 * 代理模式 — 仓库操作权限代理（在真实操作前进行角色权限校验）
 */
public class WarehouseOperationProxy implements WarehouseOperation {
    private final WarehouseOperationImpl target = new WarehouseOperationImpl();

    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "管理员", Set.of("入库", "出库", "报损", "盘点", "补货", "质检", "拣货",
                    "用户管理", "查看报表", "配置管理", "订单管理", "商品管理"),
            "仓管员", Set.of("入库", "出库", "盘点", "补货", "质检", "查看报表"),
            "拣货员", Set.of("拣货", "出库", "查看报表")
    );

    @Override
    public String operate(String operation, String role, String details) {
        Set<String> permissions = ROLE_PERMISSIONS.get(role);
        if (permissions == null || !permissions.contains(operation)) {
            return "权限不足: 角色[" + role + "]无权执行[" + operation + "]操作";
        }
        System.out.println("[权限审计] 角色:" + role + " 操作:" + operation + " 详情:" + details);
        return target.operate(operation, role, details);
    }

    public static Set<String> getPermissions(String role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Set.of());
    }
}
