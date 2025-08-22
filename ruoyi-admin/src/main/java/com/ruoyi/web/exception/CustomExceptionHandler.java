package com.ruoyi.web.exception;

import com.ruoyi.finance.utils.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义全局异常处理器
 * 统一处理Controller层的异常，返回标准化的ApiResponse响应
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 处理请求参数校验失败异常(MethodArgumentNotValidException)
     * 当DTO对象参数校验不通过时触发
     *
     * @param e 方法参数无效异常对象
     * @return 包含错误详情的ApiResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        // 收集所有字段校验错误信息
        Map<String, String> errorDetails = new HashMap<>(16);
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            // 存储格式: 字段名 -> 错误提示信息
            errorDetails.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        // 返回400状态码及错误详情
        return ApiResponse.error(400, "参数校验失败", errorDetails);
    }
}