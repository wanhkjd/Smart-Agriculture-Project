package com.origin.mapper;

import com.origin.entity.ReplenishTask;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 补货任务数据访问层
 * 提供补货任务的增删改查及联表查询
 */
@Mapper
public interface ReplenishTaskMapper {

    /**
     * 查询所有补货任务（联表商品信息）
     * */
    @Select("SELECT t.*, p.name AS product_name, p.category " +
            "FROM replenish_task t LEFT JOIN product p ON t.product_id = p.id " +
            "ORDER BY t.created_at DESC")
    List<ReplenishTask> findAllWithProduct();

    /**
     *  根据ID查询补货任务（联表）
     *  */
    @Select("SELECT t.*, p.name AS product_name, p.category " +
            "FROM replenish_task t LEFT JOIN product p ON t.product_id = p.id " +
            "WHERE t.id = #{id}")
    ReplenishTask findByIdWithProduct(Long id);

    /**
     *  新增补货任务
     *  */
    @Insert("INSERT INTO replenish_task(task_no, product_id, current_quantity, replenish_quantity, reason, creator, status) " +
            "VALUES(#{taskNo}, #{productId}, #{currentQuantity}, #{replenishQuantity}, #{reason}, #{creator}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ReplenishTask task);

    /**
     *  更新补货任务状态
     *  */
    @Update("UPDATE replenish_task SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 统计待处理的补货任务数
     * */
    @Select("SELECT COUNT(*) FROM replenish_task WHERE status = '待处理'")
    int countPending();

    /**
     * 删除补货任务
     * */
    @Delete("DELETE FROM replenish_task WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) FROM replenish_task WHERE product_id = #{productId}")
    int countByProductId(Long productId);
}
