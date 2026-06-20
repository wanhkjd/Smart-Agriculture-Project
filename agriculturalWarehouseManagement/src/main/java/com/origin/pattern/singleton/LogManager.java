package com.origin.pattern.singleton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 单例模式 — 日志管理器（全局唯一的操作日志缓存）
 */
public class LogManager {
    private static final LogManager INSTANCE = new LogManager();
    private final List<String> logs = new ArrayList<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LogManager() {}

    public static LogManager getInstance() {
        return INSTANCE;
    }

    public void addLog(String message) {
        String timestamp = LocalDateTime.now().format(FMT);
        synchronized (logs) {
            logs.add("[" + timestamp + "] " + message);
            if (logs.size() > 200) {
                logs.remove(0);
            }
        }
    }

    public List<String> getRecentLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }

    public void clear() {
        synchronized (logs) {
            logs.clear();
        }
    }
}
