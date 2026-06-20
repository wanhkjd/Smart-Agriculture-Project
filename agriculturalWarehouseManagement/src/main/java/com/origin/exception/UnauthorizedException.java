package com.origin.exception;

/**
 * 未授权异常类（错误码 401）
 * 在 JWT 解析失败、token 过期或未登录时抛出
 */
public class UnauthorizedException extends CommonException{

    /**
     * @param message 未授权原因
     */
    public UnauthorizedException(String message) {
        super(message, 401);
    }

    /**
     * @param message 未授权原因
     * @param cause 原始异常
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, 401);
    }

    /**
     * @param cause 原始异常
     */
    public UnauthorizedException(Throwable cause) {
        super(cause, 401);
    }
}
