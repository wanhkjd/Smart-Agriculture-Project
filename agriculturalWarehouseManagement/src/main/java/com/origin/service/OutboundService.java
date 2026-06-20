package com.origin.service;

import com.origin.dto.OutboundCreateDTO;
import com.origin.entity.OutboundOrder;
import java.util.List;

/**
 * 出库服务接口
 * 提供农产品出库申请、拣货确认及撤销功能
 */
public interface OutboundService {
    /**
     * 查询所有出库单
     * */
    List<OutboundOrder> findAll();
    /**
     * 根据ID查询出库单
     * */
    OutboundOrder findById(Long id);
    /**
     * 创建出库单（含拣货路径规划）
     * */
    OutboundOrder create(OutboundCreateDTO dto);
    /**
     *  移入拣货区（货物从货架移至拣货区，更新拣货路径）
     *  */
    OutboundOrder moveToPickingArea(Long id, String operator);
    /**
     *  拣货确认，扣减库存（仅管理员或分配的拣货员/仓管员可操作）
     *  */
    OutboundOrder confirmPicking(Long id, String operator);
    /**
     *  取消出库
     *  */
    void cancel(Long id, String operator);
    /**
     * 撤销上一次出库操作
     * */
    void undoLast();
    /**
     * 查询是否有可撤销的操作
     * */
    boolean canUndo();
}
