package com.origin.service.impl;

import com.origin.entity.OperationLog;
import com.origin.entity.OutboundOrder;
import com.origin.entity.PickingTask;
import com.origin.entity.User;
import com.origin.exception.BusinessException;
import com.origin.mapper.OperationLogMapper;
import com.origin.mapper.OutboundOrderMapper;
import com.origin.mapper.PickingTaskMapper;
import com.origin.mapper.UserMapper;
import com.origin.pattern.singleton.LogManager;
import com.origin.pattern.state.DeviceContext;
import com.origin.service.PickingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拣货服务实现
 * 集成状态模式管理 PDA 设备状态（空闲/作业/故障/充电）
 */
@Service
@RequiredArgsConstructor
public class PickingServiceImpl implements PickingService {
    private final PickingTaskMapper pickingTaskMapper;
    private final OutboundOrderMapper outboundMapper;
    private final OperationLogMapper logMapper;
    private final UserMapper userMapper;

    /**
     * 状态模式：PDA手持终端设备状态管理
     * */
    private final Map<String, DeviceContext> deviceStates = new HashMap<>();
    private final Map<String, String> pickerDeviceMap = new HashMap<>(); // picker → deviceId

    {
        deviceStates.put("PDA-001", new DeviceContext("PDA-001"));
        deviceStates.put("PDA-002", new DeviceContext("PDA-002"));
        deviceStates.put("PDA-003", new DeviceContext("PDA-003"));
    }

    /**
     *  查询所有拣货任务（联表商品）
     *  */
    @Override
    public List<PickingTask> findAll() {
        return pickingTaskMapper.findAllWithProduct();
    }

    /**
     * 根据ID查询拣货任务
     * */
    @Override
    public PickingTask findById(Long id) {
        PickingTask t = pickingTaskMapper.findByIdWithProduct(id);
        if (t == null) throw new BusinessException("拣货任务不存在");
        return t;
    }

    /**
     * 分配拣货员并分配空闲PDA设备，设备进入工作状态
     */
    @Override
    @Transactional
    public PickingTask assignPicker(Long id, String picker) {
        PickingTask task = findById(id);
        if (!"待分配".equals(task.getStatus())) throw new BusinessException("任务已分配");

        DeviceContext device = findAvailableDevice();
        if (device != null) {
            device.startWork();
            pickerDeviceMap.put(picker, device.getDeviceId());
        }

        task.setPicker(picker);
        task.setStatus("已分配");
        pickingTaskMapper.update(task);

        logMapper.insert(OperationLog.builder()
                .operation("分配拣货员").targetType("picking_task").targetId(id)
                .operator(picker).detail("设备:" + (device != null ? device.getDeviceId() : "无")).build());

        LogManager.getInstance().addLog("分配拣货员: " + picker + " -> " + task.getTaskNo());
        return task;
    }

    /**
     * 完成拣货任务：释放PDA设备，同步更新出库单状态
     */
    @Override
    @Transactional
    public PickingTask completePicking(Long id) {
        PickingTask task = findById(id);
        if (!"已分配".equals(task.getStatus()) && !"拣货中".equals(task.getStatus())) {
            throw new BusinessException("任务状态不允许完成");
        }

        task.setStatus("已完成");
        pickingTaskMapper.update(task);

        if (task.getOutboundOrderId() != null) {
            OutboundOrder outbound = outboundMapper.findByIdWithProduct(task.getOutboundOrderId());
            if (outbound != null && ("待拣货".equals(outbound.getStatus()) || "拣货中".equals(outbound.getStatus()))) {
                outboundMapper.updateStatus(outbound.getId(), "已拣货");
            }
        }

        freeDevice(task.getPicker());

        logMapper.insert(OperationLog.builder()
                .operation("拣货完成").targetType("picking_task").targetId(id)
                .operator(task.getPicker()).detail("任务完成").build());

        return task;
    }

    /**
     * 查询所有待处理的拣货任务
     * */
    @Override
    public List<PickingTask> findPending() {
        List<PickingTask> all = pickingTaskMapper.findAllWithProduct();
        return all.stream()
                .filter(t -> "待分配".equals(t.getStatus()) || "已分配".equals(t.getStatus()))
                .toList();
    }

    /**
     * 获取所有设备状态
     * */
    public List<DeviceContext> getDeviceStates() {
        return new ArrayList<>(deviceStates.values());
    }

    /** 获取可分配的拣货员和仓管员列表 */
    public List<User> getAvailablePickers() {
        return userMapper.findPickersAndKeepers();
    }

    /**
     * 查找可用的空闲设备
     * */
    private DeviceContext findAvailableDevice() {
        for (DeviceContext device : deviceStates.values()) {
            if ("空闲".equals(device.getStateName())) return device;
        }
        return null;
    }

    /**
     * 释放设备，恢复空闲状态
     * */
    private void freeDevice(String picker) {
        String deviceId = pickerDeviceMap.remove(picker);
        if (deviceId != null && deviceStates.containsKey(deviceId)) {
            deviceStates.get(deviceId).stopWork();
        }
    }
}
