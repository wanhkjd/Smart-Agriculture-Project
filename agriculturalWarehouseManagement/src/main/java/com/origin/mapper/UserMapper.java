package com.origin.mapper;

import com.origin.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户数据访问层
 * 提供系统用户的增删改查操作
 */
@Mapper
public interface UserMapper {

    /**
     * 查询所有用户
     * */
    @Select("SELECT * FROM user")
    List<User> findAll();

    /**
     * 根据ID查询用户
     * */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    /**
     * 根据用户名查询用户
     * */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 新增用户，返回自增主键
     * */
    @Insert("INSERT INTO user(username, password, real_name, role, phone, status) " +
            "VALUES(#{username}, #{password}, #{realName}, #{role}, #{phone}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户信息
     * */
    @Update("UPDATE user SET real_name=#{realName}, role=#{role}, phone=#{phone}, status=#{status} WHERE id=#{id}")
    int update(User user);

    /** 根据ID删除用户 */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    /** 按角色列表查询用户（拣货员+仓管员） */
    @Select("SELECT * FROM user WHERE role IN ('拣货员', '仓管员') AND status = 1")
    List<User> findPickersAndKeepers();
}
