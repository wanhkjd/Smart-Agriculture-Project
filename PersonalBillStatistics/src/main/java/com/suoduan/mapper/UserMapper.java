package com.suoduan.mapper;

import com.suoduan.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Integer id);
    int insert(User user);
    int update(User user);
}
