package com.origin.exception;

import lombok.Getter;

/**
 * 业务异常类，用于业务逻辑层面的错误提示（如库存不足、状态不允许操作等）
 * 默认错误码 500，支持自定义错误码
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    /**
     * @param message 业务错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * @param code 自定义错误码
     * @param message 业务错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
