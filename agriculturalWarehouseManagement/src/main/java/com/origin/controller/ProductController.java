package com.origin.controller;

import com.origin.common.PageResult;
import com.origin.common.Result;
import com.origin.entity.Product;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理 Controller
 * 提供农产品信息的增删改查及搜索 REST API
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final PermissionProxy permissionProxy;

    /**
     * 获取所有商品列表（传page>0时启用分页）
     * */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int pageSize) {
        var data = productService.findAll();
        return Result.success(page > 0 ? PageResult.paginate(data, page, pageSize) : (Object) data);
    }

    /**
     * 根据ID查询商品
     * */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.success(productService.findById(id));
    }

    /**
     * 按品类和关键词搜索商品
     * */
    @GetMapping("/search")
    public Result<?> search(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String keyword) {
        return Result.success(productService.search(category, keyword));
    }

    /**
     * 新增商品（代理模式校验商品管理权限）
     * */
    @PostMapping
    public Result<?> create(@RequestBody Product product) {
        permissionProxy.check("商品管理");
        return Result.success(productService.create(product));
    }

    /**
     * 更新商品信息（代理模式校验商品管理权限）
     * */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Product product) {
        permissionProxy.check("商品管理");
        product.setId(id);
        return Result.success(productService.update(product));
    }

    /**
     * 删除商品（代理模式校验商品管理权限）
     * */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        permissionProxy.check("商品管理");
        productService.delete(id);
        return Result.success();
    }
}
