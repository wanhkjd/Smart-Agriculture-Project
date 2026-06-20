package com.origin.service.impl;

import com.origin.entity.Product;
import com.origin.exception.BusinessException;
import com.origin.mapper.InventoryMapper;
import com.origin.mapper.ProductMapper;
import com.origin.pattern.singleton.LogManager;
import com.origin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务实现
 * 提供农产品信息的增删改查和搜索
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    /**
     * 查询所有启用的商品
     * */
    @Override
    public List<Product> findAll() {
        List<Product> list = productMapper.findAll();
        for (Product p : list) {
            Double stock = inventoryMapper.getTotalStockByProductId(p.getId());
            p.setTotalStock(stock != null ? stock : 0);
        }
        return list;
    }

    /**
     * 根据ID查询商品
     * */
    @Override
    public Product findById(Long id) {
        Product p = productMapper.findById(id);
        if (p == null) throw new BusinessException("商品不存在");
        return p;
    }

    /**
     *  按品类和关键词搜索商品
     *  */
    @Override
    public List<Product> search(String category, String keyword) {
        List<Product> list = productMapper.search(category, keyword);
        for (Product p : list) {
            Double stock = inventoryMapper.getTotalStockByProductId(p.getId());
            p.setTotalStock(stock != null ? stock : 0);
        }
        return list;
    }

    /**
     *  新增商品，默认设置为启用状态
     *  */
    @Override
    public Product create(Product product) {
        product.setStatus(1);
        productMapper.insert(product);
        LogManager.getInstance().addLog("新增商品: " + product.getName());
        return product;
    }

    /**
     * 更新商品信息
     * */
    @Override
    public Product update(Product product) {
        findById(product.getId());
        productMapper.update(product);
        LogManager.getInstance().addLog("更新商品: " + product.getId());
        return product;
    }

    /**
     * 根据ID删除商品
     * */
    @Override
    public void delete(Long id) {
        Double totalStock = inventoryMapper.getTotalStockByProductId(id);
        if (totalStock != null && totalStock > 0) {
            throw new BusinessException("商品还有库存（" + totalStock + "），不能删除");
        }
        productMapper.deleteById(id);
        LogManager.getInstance().addLog("删除商品: " + id);
    }
}
