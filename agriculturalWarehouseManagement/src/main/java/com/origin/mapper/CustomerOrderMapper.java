package com.origin.mapper;

import com.origin.entity.CustomerOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 客户订单数据访问层
 * 提供客户订单的增删改查及联表查询
 */
@Mapper
public interface CustomerOrderMapper {

    /**
     * 查询所有客户订单（联表商品信息）
     * */
    @Select("SELECT o.*, p.name AS product_name, p.category " +
            "FROM customer_order o LEFT JOIN product p ON o.product_id = p.id " +
            "ORDER BY o.created_at DESC")
    List<CustomerOrder> findAllWithProduct();

    /**
     * 根据ID查询客户订单（联表）
     * */
    @Select("SELECT o.*, p.name AS product_name, p.category " +
            "FROM customer_order o LEFT JOIN product p ON o.product_id = p.id " +
            "WHERE o.id = #{id}")
    CustomerOrder findByIdWithProduct(Long id);

    /**
     *  新增客户订单
     *  */
    @Insert("INSERT INTO customer_order(order_no, product_id, quantity, customer_name, customer_phone, delivery_address, status) " +
            "VALUES(#{orderNo}, #{productId}, #{quantity}, #{customerName}, #{customerPhone}, #{deliveryAddress}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerOrder order);

    /**
     *  更新客户订单状态
     *  */
    @Update("UPDATE customer_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 统计活跃订单数（非已发货、非已取消）
     * */
    @Select("SELECT COUNT(*) FROM customer_order WHERE status NOT IN ('已完成', '已发货', '已取消')")
    int countActive();

    @Select("SELECT COUNT(*) FROM customer_order WHERE product_id = #{productId}")
    int countByProductId(Long productId);
}
