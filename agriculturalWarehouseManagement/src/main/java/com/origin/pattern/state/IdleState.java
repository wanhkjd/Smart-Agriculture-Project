package com.origin.pattern.state;

/**
 * 状态模式 — 设备空闲状态
 */
public class IdleState implements DeviceState {

    @Override
    public String getStateName() { return "空闲"; }

    @Override
    public DeviceState startWork() { return new WorkingState(); }

    @Override
    public DeviceState stopWork() { return this; }

    @Override
    public DeviceState reportFault() { return new FaultState(); }

    @Override
    public DeviceState startCharge() { return new ChargingState(); }

    @Override
    public DeviceState finishCharge() { return this; }
}
