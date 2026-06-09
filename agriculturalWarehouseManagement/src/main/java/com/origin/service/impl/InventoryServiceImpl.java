package com.origin.service.impl;

import com.origin.entity.Inventory;
import com.origin.exception.BusinessException;
import com.origin.vo.LowStockVO;
import com.origin.mapper.*;
import com.origin.pattern.observer.InventorySubject;
import com.origin.pattern.observer.ReportObserver;
import com.origin.pattern.observer.WarningObserver;
import com.origin.pattern.strategy.*;
import com.origin.service.InventoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 库存服务实现
 * 集成观察者模式（库存变化通知预警/报表）和策略模式（拣货策略动态切换）
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryMapper inventoryMapper;

    /**
     * 观察者模式：库存被观察者，变化时通知预警和报表模块
     * */
    private final InventorySubject inventorySubject = new InventorySubject();

    /**
     * 策略模式：拣货策略上下文，默认 FIFO（先入先出）
     * */
    private final PickingContext pickingContext = new PickingContext(new FifoPickingStrategy());

    /**
     * 初始化观察者：注册预警观察者和报表观察者
     * */
    @PostConstruct
    public void init() {
        inventorySubject.attach(new WarningObserver());
        inventorySubject.attach(new ReportObserver());
    }

    /**
     *  查询所有库存（联表商品和货位）
     *  */
    @Override
    public List<Inventory> findAll() {
        return inventoryMapper.findAllWithProduct();
    }

    /**
     * 根据ID查询库存
     * */
    @Override
    public Inventory findById(Long id) {
        Inventory inv = inventoryMapper.findByIdWithProduct(id);
        if (inv == null) throw new BusinessException("库存记录不存在");
        return inv;
    }

    /**
     *  查询指定商品的所有批次库存
     *  */
    @Override
    public List<Inventory> findByProductId(Long productId) {
        return inventoryMapper.findByProductIdWithProduct(productId);
    }

    /**
     * 查询7天内临期库存
     * */
    @Override
    public List<Inventory> findExpiring() {
        return inventoryMapper.findExpiring();
    }

    /**
     * 查询低库存商品（按商品汇总，总库存 ≤ 50）
     * */
    @Override
    public List<LowStockVO> findLowStock() {
        return inventoryMapper.findLowStockProducts();
    }

    /**
     *  新增库存记录并通知观察者
     *  */
    @Override
    public Inventory create(Inventory inventory) {
        inventoryMapper.insert(inventory);
        inventorySubject.notifyObservers("入库", findById(inventory.getId()));
        return inventory;
    }

    /**
     * 按增量更新库存数量，并通知观察者
     * */
    @Override
    public void updateQuantity(Long id, Double delta) {
        inventoryMapper.updateQuantity(id, delta);
        Inventory inv = findById(id);
        inventorySubject.notifyObservers("库存变更", inv);
    }

    /**
     * 更新库存状态，并通知观察者
     * */
    @Override
    public void updateStatus(Long id, String status) {
        inventoryMapper.updateStatus(id, status);
        inventorySubject.notifyObservers("状态变更", findById(id));
    }

    @Override
    public String getCurrentPickingStrategy() {
        return pickingContext.getStrategyName();
    }

    /**
     * 动态切换拣货策略（FIFO/FEFO/CATEGORY）
     * */
    @Override
    public void setPickingStrategy(String strategy) {
        PickingStrategy s = switch (strategy) {
            case "FIFO" -> new FifoPickingStrategy();
            case "FEFO" -> new ShortestShelfLifeStrategy();
            case "CATEGORY" -> new CategoryPriorityStrategy();
            default -> throw new BusinessException("未知拣货策略: " + strategy);
        };
        pickingContext.setStrategy(s);
    }

    /**
     *  获取所有可用的拣货策略列表
     *  */
    @Override
    public List<String> getAvailableStrategies() {
        return List.of("FIFO", "FEFO", "CATEGORY");
    }
}
