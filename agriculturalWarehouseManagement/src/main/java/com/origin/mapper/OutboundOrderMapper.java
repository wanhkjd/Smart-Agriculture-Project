package com.origin.mapper;

import com.origin.entity.OutboundOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 出库单数据访问层
 * 提供出库单的增删改查及联表查询
 */
@Mapper
public interface OutboundOrderMapper {

    /**
     * 查询所有出库单（联表商品信息）
     * */
    @Select("SELECT o.*, p.name AS product_name, p.category " +
            "FROM outbound_order o " +
            "LEFT JOIN product p ON o.product_id = p.id " +
            "ORDER BY o.created_at DESC")
    List<OutboundOrder> findAllWithProduct();

    /**
     * 根据ID查询出库单（联表）
     * */
    @Select("SELECT o.*, p.name AS product_name, p.category " +
            "FROM outbound_order o " +
            "LEFT JOIN product p ON o.product_id = p.id " +
            "WHERE o.id = #{id}")
    OutboundOrder findByIdWithProduct(Long id);

    /**
     *  新增出库单
     *  */
    @Insert("INSERT INTO outbound_order(order_no, product_id, batch_no, quantity, picking_strategy, picking_path, " +
            "operator, status, customer_order_id) " +
            "VALUES(#{orderNo}, #{productId}, #{batchNo}, #{quantity}, #{pickingStrategy}, #{pickingPath}, " +
            "#{operator}, #{status}, #{customerOrderId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OutboundOrder order);

    /**
     *  更新出库单（拣货路径和状态）
     *  */
    @Update("UPDATE outbound_order SET picking_path=#{pickingPath}, status=#{status} WHERE id=#{id}")
    int update(OutboundOrder order);

    /**
     *  更新出库单状态
     *  */
    @Update("UPDATE outbound_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 删除出库单
     * */
    @Delete("DELETE FROM outbound_order WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计某商品已完成的出库总量
     * */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM outbound_order WHERE product_id = #{productId} AND status = '已完成'")
    Double getTotalOutboundByProductId(Long productId);

    @Select("SELECT COUNT(*) FROM outbound_order WHERE product_id = #{productId}")
    int countByProductId(Long productId);
}
