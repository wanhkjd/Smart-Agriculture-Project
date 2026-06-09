package com.origin.pattern.state;

/**
 * 状态模式 — 设备工作中状态
 */
public class WorkingState implements DeviceState {

    @Override
    public String getStateName() { return "作业中"; }

    @Override
    public DeviceState startWork() { return this; }

    @Override
    public DeviceState stopWork() { return new IdleState(); }

    @Override
    public DeviceState reportFault() { return new FaultState(); }

    @Override
    public DeviceState startCharge() { return this; }

    @Override
    public DeviceState finishCharge() { return this; }
}
