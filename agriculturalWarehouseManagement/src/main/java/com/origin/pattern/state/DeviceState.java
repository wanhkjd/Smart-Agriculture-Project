package com.origin.pattern.state;

/**
 * 状态模式 — 设备状态接口
 */
public interface DeviceState {
    String getStateName();
    DeviceState startWork();
    DeviceState stopWork();
    DeviceState reportFault();
    DeviceState startCharge();
    DeviceState finishCharge();
}
