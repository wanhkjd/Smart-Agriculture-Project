package com.suoduan.mapper;

import com.suoduan.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {
    List<Category> findAll();
    List<Category> findByType(@Param("type") String type);
}
