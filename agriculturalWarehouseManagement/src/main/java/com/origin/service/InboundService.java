package com.origin.service;

import com.origin.dto.InboundCreateDTO;
import com.origin.entity.InboundOrder;
import java.util.List;

/**
 * 入库服务接口
 * 提供农产品入库申请、质检、上架及撤销功能
 */
public interface InboundService {
    /**
     * 查询所有入库单
     * */
    List<InboundOrder> findAll();
    /**
     *  根据ID查询入库单
     *  */
    InboundOrder findById(Long id);
    /**
     *  创建入库单
     *  */
    InboundOrder create(InboundCreateDTO dto);
    /**
     * 质检确认（合格/不合格）
     * */
    InboundOrder confirmQuality(Long id, boolean passed, String operator);
    /**
     *  上架确认，生成库存记录
     *  */
    InboundOrder confirmPutaway(Long id, String locationCode, String operator);
    /**
     * 取消入库
     * */
    void cancel(Long id, String operator);
    /**
     * 撤销上一次入库操作
     * */
    void undoLast();
    /**
     * 查询是否有可撤销的操作
     * */
    boolean canUndo();
}
