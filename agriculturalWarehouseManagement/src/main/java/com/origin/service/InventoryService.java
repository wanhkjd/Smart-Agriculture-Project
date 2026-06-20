package com.origin.service;

import com.origin.entity.Inventory;
import com.origin.vo.LowStockVO;
import java.util.List;

/**
 * 库存服务接口
 * 提供库存台账管理、仪表盘统计、拣货策略切换等功能
 */
public interface InventoryService {
    /**
     * 查询所有库存记录
     * */
    List<Inventory> findAll();
    /**
     * 根据ID查询库存
     * */
    Inventory findById(Long id);
    /** 查询指定商品的所有库存批次 */
    List<Inventory> findByProductId(Long productId);
    /**
     *  查询临期库存
     */
    List<Inventory> findExpiring();
    /**
     *  查询低库存商品（按商品汇总，总库存 ≤ 50）
     * */
    List<LowStockVO> findLowStock();
    /**
     * 新增库存记录
     * */
    Inventory create(Inventory inventory);
    /**
     *  按增量更新库存数量
     *  */
    void updateQuantity(Long id, Double delta);
    /** 更新库存状态 */
    void updateStatus(Long id, String status);
    /**
     *  获取当前使用的拣货策略名称
     *  */
    String getCurrentPickingStrategy();
    /**
     *  切换拣货策略
     *  */
    void setPickingStrategy(String strategy);
    /**
     *  获取所有可用的拣货策略列表
     *  */
    List<String> getAvailableStrategies();
}
