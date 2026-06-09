package com.origin.service;

import com.origin.entity.Product;
import java.util.List;

/**
 * 商品服务接口
 * 提供农产品信息的增删改查及搜索功能
 */
public interface ProductService {
    /**
     * 查询所有启用的商品
     * */
    List<Product> findAll();
    /**
     *  根据ID查询商品
     *  */
    Product findById(Long id);
    /**
     * 按品类和关键词搜索商品
     * */
    List<Product> search(String category, String keyword);
    /**
     * 新增商品
     * */
    Product create(Product product);
    /**
     * 更新商品信息
     * */
    Product update(Product product);
    /**
     * 根据ID删除商品
     * */
    void delete(Long id);
}
