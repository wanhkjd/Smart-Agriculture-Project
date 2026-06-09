package com.origin.exception;

/**
 * 权限不足异常类（错误码 403）
 * 在用户已登录但无权执行某操作时抛出
 */
public class PermissionDeniedException extends CommonException {

    public PermissionDeniedException(String message) {
        super(message, 403);
    }
}
