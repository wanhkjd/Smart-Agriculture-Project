package com.origin.controller;

import com.origin.common.PageResult;
import com.origin.common.Result;
import com.origin.dto.InboundCreateDTO;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.InboundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 入库管理 Controller
 * 提供入库单的创建、质检、上架及撤销 REST API
 */
@RestController
@RequestMapping("/api/inbound")
@RequiredArgsConstructor
public class InboundController {
    private final InboundService inboundService;
    private final PermissionProxy permissionProxy;

    /**
     * 获取所有入库单列表（传page>0时启用分页）
     * */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int pageSize) {
        var data = inboundService.findAll();
        return Result.success(page > 0 ? PageResult.paginate(data, page, pageSize) : (Object) data);
    }

    /**
     * 根据ID查询入库单详情
     *  */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(inboundService.findById(id));
    }

    /**
     * 创建入库单（代理模式：校验入库权限）
     * */
    @PostMapping
    public Result<?> create(@Valid @RequestBody InboundCreateDTO dto) {
        permissionProxy.check("入库");
        return Result.success(inboundService.create(dto));
    }

    /**
     * 质检确认（代理模式：校验质检权限）
     * */
    @PutMapping("/{id}/quality")
    public Result<?> qualityCheck(@PathVariable Long id,
                                  @RequestParam boolean passed,
                                  @RequestParam String operator) {
        permissionProxy.check("质检");
        return Result.success(inboundService.confirmQuality(id, passed, operator));
    }

    /**
     * 上架确认（代理模式：校验入库权限）
     * */
    @PutMapping("/{id}/putaway")
    public Result<?> putaway(@PathVariable Long id,
                             @RequestParam(required = false) String locationCode,
                             @RequestParam String operator) {
        permissionProxy.check("入库");
        return Result.success(inboundService.confirmPutaway(id, locationCode, operator));
    }

    /**
     * 取消入库单（代理模式：校验入库权限）
     * */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, @RequestParam String operator) {
        permissionProxy.check("入库");
        inboundService.cancel(id, operator);
        return Result.success();
    }

    /**
     * 撤销上一次入库操作（代理模式：校验入库权限）
     * */
    @PostMapping("/undo")
    public Result<?> undo() {
        permissionProxy.check("入库");
        inboundService.undoLast();
        return Result.success("撤销成功");
    }

    /**
     * 检查是否有可撤销的入库操作
     * */
    @GetMapping("/can-undo")
    public Result<?> canUndo() {
        return Result.success(Map.of("canUndo", inboundService.canUndo()));
    }
}
