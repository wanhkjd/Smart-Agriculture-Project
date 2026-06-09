package com.origin.mapper;

import com.origin.entity.InboundOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 入库单数据访问层
 * 提供入库单的增删改查及联表查询
 */
@Mapper
public interface InboundOrderMapper {

    /**
     * 查询所有入库单（联表商品和货位信息）
     * */
    @Select("SELECT o.*, p.name AS product_name, p.category, wl.code AS location_code " +
            "FROM inbound_order o " +
            "LEFT JOIN product p ON o.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON o.location_id = wl.id " +
            "ORDER BY o.created_at DESC")
    List<InboundOrder> findAllWithProduct();

    /**
     * 根据ID查询入库单（联表）
     * */
    @Select("SELECT o.*, p.name AS product_name, p.category, wl.code AS location_code " +
            "FROM inbound_order o " +
            "LEFT JOIN product p ON o.product_id = p.id " +
            "LEFT JOIN warehouse_location wl ON o.location_id = wl.id " +
            "WHERE o.id = #{id}")
    InboundOrder findByIdWithProduct(Long id);

    /**
     * 新增入库单
     * */
    @Insert("INSERT INTO inbound_order(order_no, product_id, supplier, batch_no, quantity, location_id, " +
            "production_date, quality_check, operator, status, remark) " +
            "VALUES(#{orderNo}, #{productId}, #{supplier}, #{batchNo}, #{quantity}, #{locationId}, " +
            "#{productionDate}, #{qualityCheck}, #{operator}, #{status}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InboundOrder order);

    /**
     * 更新入库单（质检结果、货位、状态）
     * */
    @Update("UPDATE inbound_order SET quality_check=#{qualityCheck}, location_id=#{locationId}, " +
            "status=#{status}, remark=#{remark} WHERE id=#{id}")
    int update(InboundOrder order);

    /**
     * 更新入库单状态
     * */
    @Update("UPDATE inbound_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 删除入库单
     * */
    @Delete("DELETE FROM inbound_order WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计待处理的入库单数
     * */
    @Select("SELECT COUNT(*) FROM inbound_order WHERE status IN ('待质检', '质检中')")
    int countPending();

    /**
     * 统计某商品已上架的入库总量
     * */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inbound_order WHERE product_id = #{productId} AND status = '已上架'")
    Double getTotalInboundByProductId(Long productId);

    @Select("SELECT COUNT(*) FROM inbound_order WHERE product_id = #{productId}")
    int countByProductId(Long productId);
}
