package com.origin.service.impl;

import com.origin.config.JwtProperties;
import com.origin.entity.User;
import com.origin.exception.BusinessException;
import com.origin.mapper.UserMapper;
import com.origin.pattern.singleton.LogManager;
import com.origin.service.UserService;
import com.origin.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现
 * 集成 JWT 令牌生成和 LogManager 单例日志记录
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtTool jwtTool;
    private final JwtProperties jwtProperties;

    /**
     * 查询所有用户
     * */
    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    /**
     * 根据ID查询用户
     * */
    @Override
    public User findById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return user;
    }

    /**
     * 用户登录：校验用户名密码，生成 JWT token 并设置到返回值中
     * @param username 用户名
     * @param password 明文密码
     * @return 包含 token 的用户信息
     */
    @Override
    public User login(String username, String password) {

        User user = userMapper.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账户已被禁用");
        }
        String token = jwtTool.createToken(user.getId(), user.getRole(), user.getRealName(), jwtProperties.getTtlMillis());
        user.setToken(token);

        LogManager.getInstance().addLog("用户登录: " + username);
        return user;
    }

    /**
     * 创建用户，校验用户名唯一性
     * */
    @Override
    public User create(User user) {
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) throw new BusinessException("用户名已存在");
        user.setStatus(1);
        userMapper.insert(user);
        LogManager.getInstance().addLog("创建用户: " + user.getUsername());
        return user;
    }

    /**
     * 更新用户信息
     * */
    @Override
    public User update(User user) {
        userMapper.update(user);
        LogManager.getInstance().addLog("更新用户: " + user.getId());
        return user;
    }

    /**
     * 根据ID删除用户
     * */
    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
        LogManager.getInstance().addLog("删除用户: " + id);
    }
}
