package com.origin.pattern.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例模式 — 系统配置管理器（全局唯一）
 */
public class ConfigManager {
    private static volatile ConfigManager instance;
    private final Map<String, String> config = new HashMap<>();

    private ConfigManager() {
        config.put("warehouse.name", "农产品仓储中心");
        config.put("warehouse.address", "某市某区某路100号");
        config.put("picking.default_strategy", "FIFO");
        config.put("warning.expiry_days", "7");
        config.put("warning.low_stock_threshold", "50");
        config.put("page.size", "20");
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public String get(String key) {
        return config.getOrDefault(key, "");
    }

    public void set(String key, String value) {
        config.put(key, value);
    }

    public Map<String, String> getAll() {
        return new HashMap<>(config);
    }
}
