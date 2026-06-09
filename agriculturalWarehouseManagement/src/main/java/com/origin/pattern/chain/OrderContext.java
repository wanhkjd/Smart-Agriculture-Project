package com.origin.pattern.chain;

import com.origin.entity.CustomerOrder;
import lombok.Data;

/**
 * 责任链模式 — 订单处理上下文
 */
@Data
public class OrderContext {
    private CustomerOrder order;
    private boolean stockLocked;
    private boolean pickingGenerated;
    private boolean logisticsPrinted;
    private String logMessage = "";

    public OrderContext(CustomerOrder order) {
        this.order = order;
    }

    public void appendLog(String msg) {
        this.logMessage += msg + "; ";
    }
}
