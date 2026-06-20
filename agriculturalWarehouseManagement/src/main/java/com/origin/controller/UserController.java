package com.origin.controller;

import com.origin.common.PageResult;
import com.origin.common.Result;
import com.origin.entity.User;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理 Controller
 * 提供用户登录、增删改查 REST API
 * 集成代理模式，除登录接口外所有用户管理操作仅管理员可执行
 * 登录接口为白名单路径，不经过 JWT 拦截器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PermissionProxy permissionProxy;

    /**
     *  获取所有用户列表（传page>0时启用分页，代理模式校验用户管理权限）
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int pageSize) {
        permissionProxy.check("用户管理");
        var data = userService.findAll();
        return Result.success(page > 0 ? PageResult.paginate(data, page, pageSize) : (Object) data);
    }

    /**
     * 根据ID查询用户（代理模式校验用户管理权限）
     * */
    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        permissionProxy.check("用户管理");
        return Result.success(userService.findById(id));
    }

    /**
     *用户登录（白名单接口，不经过代理权限校验）
     * 返回用户基本信息及 JWT token
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params) {
        User user = userService.login(params.get("username"), params.get("password"));
        return Result.success(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "realName", user.getRealName(),
                "role", user.getRole(),
                "token", user.getToken() != null ? user.getToken() : ""
        ));
    }

    /** POST /api/users — 新增用户（代理模式校验用户管理权限）
     * */
    @PostMapping
    public Result<?> create(@RequestBody User user) {
        permissionProxy.check("用户管理");
        return Result.success(userService.create(user));
    }

    /**
     * 更新用户信息（代理模式校验用户管理权限）
     *
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody User user) {
        permissionProxy.check("用户管理");
        user.setId(id);
        return Result.success(userService.update(user));
    }

    /**
     * 删除用户（代理模式校验用户管理权限）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        permissionProxy.check("用户管理");
        userService.delete(id);
        return Result.success();
    }
}
