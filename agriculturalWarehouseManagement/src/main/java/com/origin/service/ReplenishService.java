package com.origin.service;

import com.origin.dto.ReplenishCreateDTO;
import com.origin.entity.ReplenishTask;
import java.util.List;

/**
 * 补货服务接口
 * 提供补货任务的创建、确认和管理
 */
public interface ReplenishService {
    /**
     * 查询所有补货任务
     * */
    List<ReplenishTask> findAll();
    /**
     * 根据ID查询补货任务
     * */
    ReplenishTask findById(Long id);
    /**
     * 创建补货任务
     * */
    ReplenishTask create(ReplenishCreateDTO dto);
    /**
     * 确认补货完成，更新库存
     * */
    ReplenishTask confirm(Long id);
    /**
     *  取消补货任务
     *  */
    void cancel(Long id);
    /**
     * 统计待处理补货任务数
     * */
    int countPending();
}
