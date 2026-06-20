package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志实体类
 * 映射 operation_log 表，记录仓库管理的所有关键操作审计信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
     // 日志ID
    private Long id;
     // 操作类型
    private String operation;
     // 操作目标类型（表名）
    private String targetType;
     // 操作目标ID
    private Long targetId;
     // 操作人
    private String operator;
     // 操作详情
    private String detail;
     // 操作时间
    private String createdAt;
}
