package com.origin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 货位信息实体类
 * 映射 warehouse_location 表，记录仓库货位的编号、区域、容量和负载
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocation {
    /** 货位ID */
    private Long id;
    /** 货位编码（如 C-01-01） */
    private String code;
    /** 温区：常温区/冷藏区/冷冻区 */
    private String zone;
    /** 行号 */
    private Integer rowNo;
    /** 列号 */
    private Integer colNo;
    /** 容量上限 */
    private Double capacity;
    /** 当前负载 */
    private Double currentLoad;
    /** 状态：空闲/占用/维修 */
    private String status;
    /** 创建时间 */
    private String createdAt;
}
