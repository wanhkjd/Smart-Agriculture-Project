package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统用户实体类
 * 映射 user 表，包含仓管员、拣货员、管理员三种角色
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
     // 用户ID  
    private Long id;
     // 登录用户名  
    private String username;
     // 登录密码  
    private String password;
     // 真实姓名  
    private String realName;
     // 角色：管理员/仓管员/拣货员  
    private String role;
     // 手机号  
    private String phone;
     // 状态：1启用 0禁用  
    private Integer status;
     // 创建时间  
    private String createdAt;
     // 更新时间  
    private String updatedAt;
     // 非持久化字段，仅用于登录响应返回 JWT token  
    private String token;
}
