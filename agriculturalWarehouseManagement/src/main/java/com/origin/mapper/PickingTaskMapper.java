package com.origin.mapper;

import com.origin.entity.PickingTask;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 拣货任务数据访问层
 * 提供拣货任务的增删改查及联表查询
 */
@Mapper
public interface PickingTaskMapper {

    /**
     * 查询所有拣货任务（联表商品信息）
     * */
    @Select("SELECT t.*, p.name AS product_name " +
            "FROM picking_task t LEFT JOIN product p ON t.product_id = p.id " +
            "ORDER BY t.created_at DESC")
    List<PickingTask> findAllWithProduct();

    /**
     * 根据ID查询拣货任务（联表）
     * */
    @Select("SELECT t.*, p.name AS product_name " +
            "FROM picking_task t LEFT JOIN product p ON t.product_id = p.id " +
            "WHERE t.id = #{id}")
    PickingTask findByIdWithProduct(Long id);

    /**
     * 新增拣货任务
     * */
    @Insert("INSERT INTO picking_task(task_no, outbound_order_id, product_id, quantity, picker, picking_path, status) " +
            "VALUES(#{taskNo}, #{outboundOrderId}, #{productId}, #{quantity}, #{picker}, #{pickingPath}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PickingTask task);

    /**
     * 更新拣货任务
     * */
    @Update("UPDATE picking_task SET picker=#{picker}, picking_path=#{pickingPath}, status=#{status} WHERE id=#{id}")
    int update(PickingTask task);

    /**
     * 更新拣货任务状态
     * */
    @Update("UPDATE picking_task SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     *  统计待处理的拣货任务数
     *  */
    @Select("SELECT COUNT(*) FROM picking_task WHERE status IN ('待分配', '已分配', '拣货中')")
    int countPending();

    /** 根据出库单ID查询拣货任务 */
    @Select("SELECT t.*, p.name AS product_name FROM picking_task t LEFT JOIN product p ON t.product_id = p.id WHERE t.outbound_order_id = #{outboundOrderId}")
    PickingTask findByOutboundOrderId(Long outboundOrderId);

    @Select("SELECT COUNT(*) FROM picking_task WHERE product_id = #{productId}")
    int countByProductId(Long productId);
}
