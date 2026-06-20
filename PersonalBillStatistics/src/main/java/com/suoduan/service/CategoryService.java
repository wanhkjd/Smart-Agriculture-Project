package com.suoduan.service;

import com.suoduan.entity.Category;
import com.suoduan.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryStore;

    @Autowired
    public CategoryService(CategoryMapper categoryStore) {
        this.categoryStore = categoryStore;
    }

    public List<Category> findByType(String type) {
        return categoryStore.findByType(type);
    }

    public List<Category> findAll() {
        return categoryStore.findAll();
    }
}
