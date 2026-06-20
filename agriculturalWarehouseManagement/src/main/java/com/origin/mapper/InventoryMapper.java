package com.origin.mapper;

import com.origin.entity.Inventory;
import com.origin.vo.LowStockVO;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 库存数据访问层
 * 提供库存台账的增删改查、联表查询、临期/低库存监控
 */
@Mapper
public interface InventoryMapper {

    /**
     * 查询所有库存记录（联表商品和货位信息）
     * */
    @Select("SELECT i.*, p.name AS product_name, p.category, p.storage_condition, " +
            "wl.code AS location_code " +
            "FROM inventory i " +
            "LEFT JOIN product p ON i.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON i.location_id = wl.id")
    List<Inventory> findAllWithProduct();

    /**
     * 根据ID查询库存（联表）
     * */
    @Select("SELECT i.*, p.name AS product_name, p.category, p.storage_condition, " +
            "wl.code AS location_code " +
            "FROM inventory i " +
            "LEFT JOIN product p ON i.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON i.location_id = wl.id " +
            "WHERE i.id = #{id}")
    Inventory findByIdWithProduct(Long id);

    /**
     * 根据商品ID查询该商品的所有库存批次
     * */
    @Select("SELECT i.*, p.name AS product_name, p.category, p.storage_condition, " +
            "wl.code AS location_code " +
            "FROM inventory i " +
            "LEFT JOIN product p ON i.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON i.location_id = wl.id " +
            "WHERE i.product_id = #{productId}")
    List<Inventory> findByProductIdWithProduct(Long productId);

    /**
     * 查询7天内即将过期的库存（临期预警）
     * */
    @Select("SELECT i.*, p.name AS product_name, p.category, p.storage_condition, " +
            "wl.code AS location_code " +
            "FROM inventory i " +
            "LEFT JOIN product p ON i.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON i.location_id = wl.id " +
            "WHERE i.expiry_date <= DATE_ADD(NOW(), INTERVAL 7 DAY) AND i.status != '过期'")
    List<Inventory> findExpiring();

    /**
     * 查询库存不足（低于10）的记录
     * */
    @Select("SELECT i.*, p.name AS product_name, p.category, p.storage_condition, " +
            "wl.code AS location_code " +
            "FROM inventory i " +
            "LEFT JOIN product p ON i.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON i.location_id = wl.id " +
            "WHERE i.quantity <= 10 AND i.status = '正常'")
    List<Inventory> findLowStock();

    /**
     * 新增库存记录
     * */
    @Insert("INSERT INTO inventory(product_id, batch_no, quantity, location_id, shelf_no, " +
            "production_date, expiry_date, status) " +
            "VALUES(#{productId}, #{batchNo}, #{quantity}, #{locationId}, #{shelfNo}, " +
            "#{productionDate}, #{expiryDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Inventory inventory);

    /**
     * 更新库存信息
     * */
    @Update("UPDATE inventory SET quantity = #{quantity}, location_id = #{locationId}, status = #{status} WHERE id = #{id}")
    int update(Inventory inventory);

    /**
     * 按增量更新库存数量（正数为入库，负数为出库）
     * */
    @Update("UPDATE inventory SET quantity = quantity + #{delta} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("delta") Double delta);

    /**
     * 更新库存状态
     * */
    @Update("UPDATE inventory SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     *  根据ID删除库存记录
     *  */
    @Delete("DELETE FROM inventory WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 查询库存记录的当前数量
     * */
    @Select("SELECT quantity FROM inventory WHERE id = #{id}")
    Double getQuantityById(Long id);

    /**
     * 统计某商品的总库存量（排除过期）
     * */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory WHERE product_id = #{productId} AND status != '过期'")
    Double getTotalStockByProductId(Long productId);

    /**
     * 统计临期库存数
     * */
    @Select("SELECT COUNT(*) FROM inventory WHERE expiry_date <= DATE_ADD(NOW(), INTERVAL 7 DAY) AND status != '过期'")
    int countExpiring();

    /**
     * 统计低库存商品数（按商品汇总，总库存 ≤ 50）
     * */
    @Select("SELECT COUNT(*) FROM (SELECT p.id FROM product p " +
            "LEFT JOIN inventory i ON p.id = i.product_id AND i.status != '过期' " +
            "GROUP BY p.id HAVING COALESCE(SUM(i.quantity), 0) <= 50) t")
    int countLowStockProducts();

    /**
     * 查询低库存商品（按商品汇总，总库存 ≤ 50）
     * */
    @Select("SELECT p.id AS product_id, p.name AS product_name, p.category, " +
            "p.storage_condition, p.unit, " +
            "COALESCE(SUM(i.quantity), 0) AS total_quantity " +
            "FROM product p " +
            "LEFT JOIN inventory i ON p.id = i.product_id AND i.status != '过期' " +
            "GROUP BY p.id, p.name, p.category, p.storage_condition, p.unit " +
            "HAVING COALESCE(SUM(i.quantity), 0) <= 50")
    @Results(id = "lowStockResult", value = {
            @Result(column = "product_id", property = "productId"),
            @Result(column = "product_name", property = "productName"),
            @Result(column = "category", property = "category"),
            @Result(column = "storage_condition", property = "storageCondition"),
            @Result(column = "unit", property = "unit"),
            @Result(column = "total_quantity", property = "totalQuantity"),
    })
    List<LowStockVO> findLowStockProducts();

    /**
     * 统计库存总记录数
     * */
    @Select("SELECT COUNT(*) FROM inventory")
    int countAll();

    /**
     * 统计某商品临期库存数（7天内到期）
     * */
    @Select("SELECT COUNT(*) FROM inventory WHERE product_id = #{productId} AND expiry_date <= DATE_ADD(NOW(), INTERVAL 7 DAY) AND status != '过期'")
    int countExpiringByProductId(Long productId);

    /**
     * 统计某商品已过期的库存数
     * */
    @Select("SELECT COUNT(*) FROM inventory WHERE product_id = #{productId} AND status = '过期'")
    int countExpiredByProductId(Long productId);

    @Select("SELECT * FROM inventory WHERE product_id = #{productId} AND status = '锁定'")
    List<Inventory> findLockedByProductId(Long productId);

    @Update("UPDATE inventory SET status = '正常' WHERE product_id = #{productId} AND status = '锁定'")
    int unlockByProductId(Long productId);
}
