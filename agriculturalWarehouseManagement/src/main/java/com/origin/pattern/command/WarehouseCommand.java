package com.origin.pattern.command;

/**
 * 命令模式 — 仓库操作命令接口
 */
public interface WarehouseCommand {
    void execute();
    void undo();
    String getDescription();
}
