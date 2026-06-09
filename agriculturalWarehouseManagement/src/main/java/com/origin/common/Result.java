package com.origin.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一响应体，封装所有 API 返回结果
 * @param <T> 响应数据类型
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    /**
     * 返回成功（无数据）
     * @return 操作成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 返回成功（带数据）
     * @param data 响应数据
     * @return 操作成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 返回成功（自定义消息 + 数据）
     * @param message 成功消息
     * @param data 响应数据
     * @return 操作成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 返回错误（自定义状态码 + 消息）
     * @param code 错误码
     * @param message 错误消息
     * @return 错误结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 返回错误（默认500状态码）
     * @param message 错误消息
     * @return 错误结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
