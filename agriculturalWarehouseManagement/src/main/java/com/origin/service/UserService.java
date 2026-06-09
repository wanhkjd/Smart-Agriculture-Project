package com.origin.service;

import com.origin.entity.User;
import java.util.List;

/**
 * 用户服务接口
 * 提供系统用户的增删改查及登录认证功能
 */
public interface UserService {

    /**
     * 查询所有用户
     * */
    List<User> findAll();
    /**
     * 根据ID查询用户
     * */
    User findById(Long id);
    /**
     * 用户名密码登录，返回包含token的用户信息
     * */
    User login(String username, String password);
    /**
     * 新增用户
     * */
    User create(User user);
    /**
     * 更新用户信息
     * */
    User update(User user);
    /**
     * 根据ID删除用户
     * */
    void delete(Long id);
}
