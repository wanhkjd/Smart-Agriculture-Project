package com.origin.controller;

import com.origin.common.PageResult;
import com.origin.common.Result;
import com.origin.dto.OrderProcessDTO;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户订单管理 Controller
 * 提供客户订单的创建、处理（责任链模式）和状态管理 REST API
 * 集成代理模式进行订单管理权限校验
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PermissionProxy permissionProxy;

    /**
     * 获取所有客户订单列表（传page>0时启用分页）
     * */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int pageSize) {
        var data = orderService.findAll();
        return Result.success(page > 0 ? PageResult.paginate(data, page, pageSize) : (Object) data);
    }

    /**
     * 根据ID查询订单详情
     * */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(orderService.findById(id));
    }

    /**
     * 创建客户订单（代理模式校验订单管理权限）
     * */
    @PostMapping
    public Result<?> create(@Valid @RequestBody OrderProcessDTO dto) {
        permissionProxy.check("订单管理");
        return Result.success(orderService.create(dto));
    }

    /**
     * 处理订单（责任链流水线，代理模式校验订单管理权限）
     * */
    @PostMapping("/{id}/process")
    public Result<?> process(@PathVariable Long id) {
        permissionProxy.check("订单管理");
        return Result.success(orderService.processOrder(id));
    }

    /**
     * 更新订单状态
     * */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        permissionProxy.check("订单管理");
        return Result.success(orderService.updateStatus(id, status));
    }

    /**
     * 取消订单
     * */
    @PutMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id) {
        permissionProxy.check("订单管理");
        orderService.cancel(id);
        return Result.success();
    }
}
