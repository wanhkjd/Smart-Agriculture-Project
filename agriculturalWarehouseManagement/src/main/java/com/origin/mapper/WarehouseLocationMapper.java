package com.origin.mapper;

import com.origin.entity.WarehouseLocation;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 货位数据访问层
 * 提供仓库货位的查询、状态更新和负载管理
 */
@Mapper
public interface WarehouseLocationMapper {

    /**
     * 查询所有货位
     * */
    @Select("SELECT * FROM warehouse_location")
    List<WarehouseLocation> findAll();

    /**
     *  根据ID查询货位
     *  */
    @Select("SELECT * FROM warehouse_location WHERE id = #{id}")
    WarehouseLocation findById(Long id);

    /**
     *  查询指定温区下可用的货位
     *  */
    @Select("SELECT * FROM warehouse_location WHERE zone = #{zone} AND status = '空闲'")
    List<WarehouseLocation> findAvailableByZone(String zone);

    /**
     * 查找第一个空闲货位
     * */
    @Select("SELECT * FROM warehouse_location WHERE status = '空闲' LIMIT 1")
    WarehouseLocation findFirstAvailable();

    /**
     * 更新货位负载（delta 正数为入库，负数为出库）
     * */
    @Update("UPDATE warehouse_location SET current_load = current_load + #{delta}, " +
            "status = CASE WHEN current_load + #{delta} >= capacity THEN '占用' ELSE status END " +
            "WHERE id = #{id}")
    int updateLoad(@Param("id") Long id, @Param("delta") Double delta);

    /**
     * 更新货位状态
     * */
    @Update("UPDATE warehouse_location SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 新增货位
     * */
    @Insert("INSERT INTO warehouse_location(code, zone, row_no, col_no, capacity, current_load, status) " +
            "VALUES(#{code}, #{zone}, #{rowNo}, #{colNo}, #{capacity}, #{currentLoad}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WarehouseLocation location);
}
