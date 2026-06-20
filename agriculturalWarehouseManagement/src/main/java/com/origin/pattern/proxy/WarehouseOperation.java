package com.origin.pattern.proxy;

/**
 * 代理模式 — 仓库操作接口
 */
public interface WarehouseOperation {
    String operate(String operation, String role, String details);
}
