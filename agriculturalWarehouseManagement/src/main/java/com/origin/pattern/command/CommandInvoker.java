package com.origin.pattern.command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 命令模式 — 命令调用者（支持执行队列和执行历史+撤销）
 */
public class CommandInvoker {
    private final List<WarehouseCommand> pendingQueue = new ArrayList<>();
    private final Deque<WarehouseCommand> executedStack = new ArrayDeque<>();

    public void enqueue(WarehouseCommand command) {
        pendingQueue.add(command);
    }

    public void executeAll() {
        for (WarehouseCommand cmd : pendingQueue) {
            cmd.execute();
            executedStack.push(cmd);
        }
        pendingQueue.clear();
    }

    public void execute(WarehouseCommand command) {
        command.execute();
        executedStack.push(command);
    }

    public void undoLast() {
        if (!executedStack.isEmpty()) {
            WarehouseCommand cmd = executedStack.pop();
            cmd.undo();
        }
    }

    public boolean canUndo() {
        return !executedStack.isEmpty();
    }

    public int getPendingCount() {
        return pendingQueue.size();
    }

    public List<WarehouseCommand> getPendingCommands() {
        return new ArrayList<>(pendingQueue);
    }
}
