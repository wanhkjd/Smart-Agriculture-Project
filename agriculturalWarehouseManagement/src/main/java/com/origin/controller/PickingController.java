package com.origin.controller;

import com.origin.common.Result;
import com.origin.pattern.state.DeviceContext;
import com.origin.service.PickingService;
import com.origin.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 拣货任务管理 Controller — 集成状态模式展示设备状态
 */
@RestController
@RequestMapping("/api/picking")
@RequiredArgsConstructor
public class PickingController {
    private final PickingService pickingService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        var data = pickingService.findAll();
        return Result.success(PageUtils.paginate(data, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(pickingService.findById(id));
    }

    @GetMapping("/pending")
    public Result<?> pending() {
        return Result.success(pickingService.findPending());
    }

    @PutMapping("/{id}/assign")
    public Result<?> assign(@PathVariable Long id, @RequestParam String picker) {
        return Result.success(pickingService.assignPicker(id, picker));
    }

    @PutMapping("/{id}/complete")
    public Result<?> complete(@PathVariable Long id) {
        return Result.success(pickingService.completePicking(id));
    }

    /** 状态模式：获取所有 PDA 设备状态 */
    @GetMapping("/devices")
    public Result<?> devices() {
        return Result.success(((com.origin.service.impl.PickingServiceImpl) pickingService).getDeviceStates());
    }

    /** 获取可分配的拣货员和仓管员列表（用于分配拣货员弹窗） */
    @GetMapping("/available-pickers")
    public Result<?> availablePickers() {
        return Result.success(((com.origin.service.impl.PickingServiceImpl) pickingService).getAvailablePickers());
    }
}
