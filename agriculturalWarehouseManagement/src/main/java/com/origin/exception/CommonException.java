package com.origin.exception;

import lombok.Getter;

/**
 * 通用业务异常类，作为项目中所有自定义异常的基础
 * 包含错误码 + 错误消息，由 GlobalExceptionHandler 统一处理
 */
@Getter
public class CommonException extends RuntimeException{
    private int code;

    /**
     * @param message 错误消息
     * @param code 错误码
     */
    public CommonException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * @param message 错误消息
     * @param cause 原始异常
     * @param code 错误码
     */
    public CommonException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    /**
     * @param cause 原始异常
     * @param code 错误码
     */
    public CommonException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }
}
