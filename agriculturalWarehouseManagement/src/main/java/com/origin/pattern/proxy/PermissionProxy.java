package com.origin.pattern.proxy;

import com.origin.exception.PermissionDeniedException;
import com.origin.exception.UnauthorizedException;
import com.origin.utils.UserContext;
import org.springframework.stereotype.Component;

/**
 * 代理模式 — 权限校验代理服务
 * 在 Controller 调用业务操作前进行角色权限校验，
 * 权限不足时抛出 PermissionDeniedException（前端显示"权限不足"），
 * 权限通过则放行由真实业务逻辑执行。
 *
 * 权限矩阵（定义在 WarehouseOperationProxy 中）：
 *   管理员：全部操作
 *   仓管员：入库、出库、盘点、补货、质检、查看报表
 *   拣货员：拣货、出库
 */
@Component
public class PermissionProxy {

    private final WarehouseOperationProxy proxy = new WarehouseOperationProxy();

    /**
     * 校验当前登录用户是否有权限执行指定操作
     * @param operation 操作名称（如"入库"、"出库"、"用户管理"等）
     * @throws PermissionDeniedException 权限不足时抛出
     */
    public void check(String operation) {
        String role = UserContext.getRole();
        if (role == null) {
            throw new UnauthorizedException("未登录，无法校验权限");
        }
        String result = proxy.operate(operation, role, "Proxy校验");
        if (result != null && result.contains("权限不足")) {
            throw new PermissionDeniedException(result);
        }
    }
}
