package com.origin.service;

import com.origin.entity.PickingTask;
import java.util.List;

/**
 * 拣货服务接口
 * 提供拣货任务的分配和执行管理
 */
public interface PickingService {
    /**
     * 查询所有拣货任务
     * */
    List<PickingTask> findAll();
    /**
     * 根据ID查询拣货任务
     * */
    PickingTask findById(Long id);
    /**
     * 分配拣货员
     * */
    PickingTask assignPicker(Long id, String picker);
    /**
     *  完成拣货任务
     *  */
    PickingTask completePicking(Long id);
    /**
     * 查询所有待处理的拣货任务
     * */
    List<PickingTask> findPending();
}
