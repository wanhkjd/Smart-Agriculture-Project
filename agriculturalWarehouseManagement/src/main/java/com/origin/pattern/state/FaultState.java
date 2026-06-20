package com.origin.pattern.state;

/**
 * 状态模式 — 设备故障状态
 */
public class FaultState implements DeviceState {

    @Override
    public String getStateName() { return "故障"; }

    @Override
    public DeviceState startWork() { return this; }

    @Override
    public DeviceState stopWork() { return this; }

    @Override
    public DeviceState reportFault() { return this; }

    @Override
    public DeviceState startCharge() { return this; }

    @Override
    public DeviceState finishCharge() { return new IdleState(); }
}
