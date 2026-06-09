package com.origin.service.impl;

import com.origin.dto.ReplenishCreateDTO;
import com.origin.entity.*;
import com.origin.exception.BusinessException;
import com.origin.mapper.*;
import com.origin.pattern.factory.DocumentFactory;
import com.origin.pattern.factory.WarehouseDocument;
import com.origin.pattern.singleton.LogManager;
import com.origin.pattern.strategy.ColdChainStorageStrategy;
import com.origin.pattern.strategy.NormalStorageStrategy;
import com.origin.pattern.strategy.StorageContext;
import com.origin.service.ReplenishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 补货服务实现
 * 集成工厂方法模式创建补货单据
 */
@Service
@RequiredArgsConstructor
public class ReplenishServiceImpl implements ReplenishService {
    private final ReplenishTaskMapper replenishMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final OperationLogMapper logMapper;
    private final WarehouseLocationMapper locationMapper;

    /**
     * 查询所有补货任务（联表商品）
     * */
    @Override
    public List<ReplenishTask> findAll() {
        return replenishMapper.findAllWithProduct();
    }

    /**
     *  根据ID查询补货任务
     *  */
    @Override
    public ReplenishTask findById(Long id) {
        ReplenishTask t = replenishMapper.findByIdWithProduct(id);
        if (t == null) throw new BusinessException("补货任务不存在");
        return t;
    }

    /**
     * 创建补货任务：使用工厂方法创建补货单据，记录当前库存量
     */
    @Override
    @Transactional
    public ReplenishTask create(ReplenishCreateDTO dto) {
        Product product = productMapper.findById(dto.getProductId());
        if (product == null) throw new BusinessException("商品不存在");

        WarehouseDocument doc = DocumentFactory.createDocument("REPLENISH", dto.getProductId(), dto.getReplenishQuantity());

        Double currentStock = inventoryMapper.getTotalStockByProductId(dto.getProductId());

        ReplenishTask task = ReplenishTask.builder()
                .taskNo(doc.getDocNo())
                .productId(dto.getProductId())
                .currentQuantity(currentStock)
                .replenishQuantity(dto.getReplenishQuantity())
                .reason(dto.getReason())
                .creator(dto.getCreator())
                .status("待处理")
                .build();
        replenishMapper.insert(task);

        logMapper.insert(OperationLog.builder()
                .operation("创建补货任务").targetType("replenish_task").targetId(task.getId())
                .operator(dto.getCreator()).detail("当前库存:" + currentStock + " 补货数量:" + dto.getReplenishQuantity()).build());

        LogManager.getInstance().addLog("创建补货任务: " + product.getName() + " 补货" + dto.getReplenishQuantity());
        return task;
    }

    /**
     * 确认补货完成：创建新库存批次记录（含生产日期、保质期计算、货位分配）
     * */
    @Override
    @Transactional
    public ReplenishTask confirm(Long id) {
        ReplenishTask task = findById(id);
        if (!"待处理".equals(task.getStatus()) && !"补货中".equals(task.getStatus())) {
            throw new BusinessException("当前状态不允许确认补货");
        }

        Product product = productMapper.findById(task.getProductId());

        // 策略模式：根据存储条件分配货位
        StorageContext storageCtx = product.getStorageCondition() != null
                && ("冷藏".equals(product.getStorageCondition()) || "冷冻".equals(product.getStorageCondition()))
                ? new StorageContext(new ColdChainStorageStrategy())
                : new StorageContext(new NormalStorageStrategy());
        List<WarehouseLocation> locations = locationMapper.findAll();
        WarehouseLocation location = storageCtx.assignLocation(locations);

        WarehouseDocument doc = DocumentFactory.createDocument("REPLENISH", task.getProductId(), task.getReplenishQuantity());
        String productionDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusDays(product.getShelfLifeDays()).toString();

        Inventory inventory = Inventory.builder()
                .productId(task.getProductId())
                .batchNo(doc.getDocNo())
                .quantity(task.getReplenishQuantity())
                .locationId(location != null ? location.getId() : null)
                .shelfNo(location != null ? location.getCode() : null)
                .productionDate(productionDate)
                .expiryDate(expiryDate)
                .status("正常")
                .build();
        inventoryMapper.insert(inventory);

        task.setStatus("已完成");
        replenishMapper.updateStatus(task.getId(), "已完成");

        logMapper.insert(OperationLog.builder()
                .operation("补货完成").targetType("replenish_task").targetId(id)
                .operator(task.getCreator()).detail("补货数量:" + task.getReplenishQuantity()
                        + " 批次:" + doc.getDocNo() + " 生产日期:" + productionDate + " 过期日期:" + expiryDate).build());

        LogManager.getInstance().addLog("补货完成: " + product.getName() + " +" + task.getReplenishQuantity() + " 批次" + doc.getDocNo());
        return task;
    }

    /**
     * 取消补货任务
     * */
    @Override
    public void cancel(Long id) {
        replenishMapper.updateStatus(id, "已取消");
    }

    /**
     *  统计待处理的补货任务数
     *  */
    @Override
    public int countPending() {
        return replenishMapper.countPending();
    }
}
