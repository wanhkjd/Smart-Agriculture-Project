package com.origin.controller;

import com.origin.common.PageResult;
import com.origin.common.Result;
import com.origin.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 库存管理 Controller
 * 提供库存台账查询、临期/低库存预警、拣货策略切换 REST API
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * 获取所有库存记录（支持分页：传page>0时启用分页）
     * */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int pageSize) {
        var data = inventoryService.findAll();
        return Result.success(page > 0 ? PageResult.paginate(data, page, pageSize) : (Object) data);
    }

    /**
     * 根据ID查询库存详情
     * */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(inventoryService.findById(id));
    }

    /**
     * 查询临期库存
     * */
    @GetMapping("/expiring")
    public Result<?> expiring() {
        return Result.success(inventoryService.findExpiring());
    }

    /**
     * 查询低库存记录
     * */
    @GetMapping("/low-stock")
    public Result<?> lowStock() {
        return Result.success(inventoryService.findLowStock());
    }

    /**
     * 查询某商品的所有批次库存
     * */
    @GetMapping("/by-product/{productId}")
    public Result<?> byProduct(@PathVariable Long productId) {
        return Result.success(inventoryService.findByProductId(productId));
    }

    /**
     *  更新库存数量
     *  */
    @PutMapping("/{id}/quantity")
    public Result<?> updateQuantity(@PathVariable Long id, @RequestParam Double delta) {
        inventoryService.updateQuantity(id, delta);
        return Result.success();
    }

    /**
     * 获取当前拣货策略
     * */
    @GetMapping("/strategy")
    public Result<?> getPickingStrategy() {
        return Result.success(Map.of(
                "current", inventoryService.getCurrentPickingStrategy(),
                "available", inventoryService.getAvailableStrategies()
        ));
    }

    /**
     * 切换拣货策略
     * */
    @PutMapping("/strategy")
    public Result<?> setPickingStrategy(@RequestParam String type) {
        inventoryService.setPickingStrategy(type);
        return Result.success("已切换为: " + type);
    }
}
