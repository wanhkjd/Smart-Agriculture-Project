package com.suoduan.controller;

import com.suoduan.entity.Category;
import com.suoduan.entity.Result;
import com.suoduan.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryCatalog;

    @Autowired
    public CategoryController(CategoryService categoryCatalog) {
        this.categoryCatalog = categoryCatalog;
    }

    @GetMapping
    public Result<List<Category>> list(@RequestParam(required = false) String type) {
        List<Category> categories = hasText(type)
                ? categoryCatalog.findByType(type)
                : categoryCatalog.findAll();
        return Result.ok(categories);
    }

    private boolean hasText(String value) {
        return value != null && !value.isEmpty();
    }
}
