package com.origin.mapper;

import com.origin.entity.OperationLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 操作日志数据访问层
 * 提供操作日志的记录和查询
 */
@Mapper
public interface OperationLogMapper {

    /**
     *  查询最近100条操作日志
     *  */
    @Select("SELECT * FROM operation_log ORDER BY created_at DESC LIMIT 100")
    List<OperationLog> findRecent();

    /**
     * 查询全部操作日志
     * */
    @Select("SELECT * FROM operation_log ORDER BY created_at DESC")
    List<OperationLog> findAll();

    /**
     * 新增操作日志
     * */
    @Insert("INSERT INTO operation_log(operation, target_type, target_id, operator, detail) " +
            "VALUES(#{operation}, #{targetType}, #{targetId}, #{operator}, #{detail})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    /**
     * 统计今日某类操作的数量
     * */
    @Select("SELECT COUNT(*) FROM operation_log WHERE operation = #{operation} AND DATE(created_at) = CURDATE()")
    int countTodayByOperation(String operation);
}
