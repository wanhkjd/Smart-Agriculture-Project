package com.origin.pattern.state;

/**
 * 状态模式 — 设备上下文（维护设备当前状态）
 */
public class DeviceContext {
    private String deviceId;
    private DeviceState state;

    public DeviceContext(String deviceId) {
        this.deviceId = deviceId;
        this.state = new IdleState();
    }

    public String getDeviceId() { return deviceId; }
    public String getStateName() { return state.getStateName(); }

    public void startWork() { state = state.startWork(); }
    public void stopWork() { state = state.stopWork(); }
    public void reportFault() { state = state.reportFault(); }
    public void startCharge() { state = state.startCharge(); }
    public void finishCharge() { state = state.finishCharge(); }
}
