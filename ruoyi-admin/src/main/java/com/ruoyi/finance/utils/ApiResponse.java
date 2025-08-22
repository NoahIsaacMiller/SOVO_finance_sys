package com.ruoyi.finance.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义API响应封装类
 * @param <T> 响应数据类型
 */
@Data
public class ApiResponse<T> implements Serializable {
    // 状态码：200成功，其他为错误
    private int code;
    // 响应消息
    private String message;
    // 响应数据
    private T data;
    // 时间戳
    private long timestamp;
    private T errors;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 错误响应（带数据）
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setErrors(data);
        return response;
    }

    /**
     * 错误响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return error(code, message, null);
    }

    /**
     * 错误响应（默认500状态码）
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }
}