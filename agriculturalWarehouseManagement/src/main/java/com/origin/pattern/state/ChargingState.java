package com.origin.pattern.state;

/**
 * 状态模式 — 设备充电中状态
 */
public class ChargingState implements DeviceState {

    @Override
    public String getStateName() { return "充电中"; }

    @Override
    public DeviceState startWork() { return this; }

    @Override
    public DeviceState stopWork() { return this; }

    @Override
    public DeviceState reportFault() { return new FaultState(); }

    @Override
    public DeviceState startCharge() { return this; }

    @Override
    public DeviceState finishCharge() { return new IdleState(); }
}
