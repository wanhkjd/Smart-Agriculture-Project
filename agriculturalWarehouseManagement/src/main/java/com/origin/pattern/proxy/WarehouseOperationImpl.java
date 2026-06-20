package com.origin.pattern.proxy;

/**
 * 代理模式 — 仓库操作真实实现
 */
public class WarehouseOperationImpl implements WarehouseOperation {

    @Override
    public String operate(String operation, String role, String details) {
        return "操作成功: " + operation + " - " + details;
    }
}
