package com.origin.controller;

import com.origin.common.Result;
import com.origin.dto.OutboundCreateDTO;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.OutboundService;
import com.origin.utils.PageUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 出库管理 Controller
 * 提供出库单的创建、拣货确认及撤销 REST API
 * 集成代理模式进行出库/拣货操作权限校验
 */
@RestController
@RequestMapping("/api/outbound")
@RequiredArgsConstructor
public class OutboundController {
    private final OutboundService outboundService;
    private final PermissionProxy permissionProxy;

    /**
     * 获取所有出库单列表（传page>0时启用分页）
     * */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        var data = outboundService.findAll();
        return Result.success(PageUtils.paginate(data, page, pageSize));
    }

    /**
     * 根据ID查询出库单详情
     * */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(outboundService.findById(id));
    }

    /**
     *  创建出库单（含拣货路径规划，代理模式校验出库权限）
     *  */
    @PostMapping
    public Result<?> create(@Valid @RequestBody OutboundCreateDTO dto) {
        permissionProxy.check("出库");
        return Result.success(outboundService.create(dto));
    }

    /**
     * 移入拣货区（货物从货架移至拣货区）
     * */
    @PutMapping("/{id}/move")
    public Result<?> moveToPickingArea(@PathVariable Long id, @RequestParam String operator) {
        permissionProxy.check("出库");
        return Result.success(outboundService.moveToPickingArea(id, operator));
    }

    /**
     *  拣货确认，扣减库存（仅管理员或被分配的拣货员/仓管员可操作）
     *  */
    @PutMapping("/{id}/confirm")
    public Result<?> confirmPicking(@PathVariable Long id, @RequestParam String operator) {
        permissionProxy.check("拣货");
        return Result.success(outboundService.confirmPicking(id, operator));
    }

    /**
     * 取消出库单（代理模式校验出库权限）
     * */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, @RequestParam String operator) {
        permissionProxy.check("出库");
        outboundService.cancel(id, operator);
        return Result.success();
    }

    /**
     * 撤销上一次出库操作（代理模式校验出库权限）
     * */
    @PostMapping("/undo")
    public Result<?> undo() {
        permissionProxy.check("出库");
        outboundService.undoLast();
        return Result.success("撤销成功");
    }

    /**
     * 检查是否有可撤销的出库操作
     * */
    @GetMapping("/can-undo")
    public Result<?> canUndo() {
        return Result.success(Map.of("canUndo", outboundService.canUndo()));
    }
}
