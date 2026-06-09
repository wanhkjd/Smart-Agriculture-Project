package com.origin.controller;

import com.origin.common.PageResult;
import com.origin.common.Result;
import com.origin.dto.ReplenishCreateDTO;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.ReplenishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 补货任务管理 Controller
 * 提供补货任务的创建、确认及统计 REST API
 * 集成代理模式进行补货权限校验
 */
@RestController
@RequestMapping("/api/replenish")
@RequiredArgsConstructor
public class ReplenishController {
    private final ReplenishService replenishService;
    private final PermissionProxy permissionProxy;

    /**
     * 获取所有补货任务列表（传page>0时启用分页）
     * */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int pageSize) {
        var data = replenishService.findAll();
        return Result.success(page > 0 ? PageResult.paginate(data, page, pageSize) : (Object) data);
    }

    /**
     *  根据ID查询补货任务详情
     *  */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(replenishService.findById(id));
    }

    /**
     * 创建补货任务（代理模式校验补货权限）
     * */
    @PostMapping
    public Result<?> create(@Valid @RequestBody ReplenishCreateDTO dto) {
        permissionProxy.check("补货");
        return Result.success(replenishService.create(dto));
    }

    /**
     * 确认补货完成（代理模式校验补货权限）
     */
    @PutMapping("/{id}/confirm")
    public Result<?> confirm(@PathVariable Long id) {
        permissionProxy.check("补货");
        return Result.success(replenishService.confirm(id));
    }

    /**
     * 取消补货任务（代理模式校验补货权限）
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id) {
        permissionProxy.check("补货");
        replenishService.cancel(id);
        return Result.success();
    }

    /**
     * 获取待处理补货任务数
     */
    @GetMapping("/pending-count")
    public Result<?> pendingCount() {
        return Result.success(Map.of("count", replenishService.countPending()));
    }
}
