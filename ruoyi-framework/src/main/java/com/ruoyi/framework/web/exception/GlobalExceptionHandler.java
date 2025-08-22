package com.ruoyi.framework.web.exception;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.DemoModeException;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.html.EscapeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String username = getCurrentUsername(); // 假设存在获取当前用户的方法

        // 详细日志包含：时间、用户、请求方法、请求地址、异常信息、堆栈
        log.error("[{}] 权限校验失败 - 用户: {}, 请求方法: {}, 请求地址: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                username,
                method,
                requestURI,
                e.getMessage(),
                e);

        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                          HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        // 获取支持的请求方法
        String supportedMethods = StringUtils.join(e.getSupportedMethods(), ", ");

        log.error("[{}] 请求方法不支持 - 请求方法: {}, 请求地址: {}, 支持的方法: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                method,
                requestURI,
                supportedMethods,
                e.getMessage(),
                e);

        return AjaxResult.error(String.format("请求方法不支持 %s，支持的方法为: %s", method, supportedMethods));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.error("[{}] 业务异常 - 请求方法: {}, 请求地址: {}, 错误码: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                method,
                requestURI,
                e.getCode(),
                e.getMessage(),
                e);

        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public AjaxResult handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.error("[{}] 路径变量缺失 - 请求方法: {}, 请求地址: {}, 缺失变量名: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                method,
                requestURI,
                e.getVariableName(),
                e.getMessage(),
                e);

        return AjaxResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String value = Convert.toStr(e.getValue());
        if (StringUtils.isNotEmpty(value))
        {
            value = EscapeUtil.clean(value);
        }

        log.error("[{}] 参数类型不匹配 - 请求方法: {}, 请求地址: {}, 参数名: {}, 期望类型: {}, 实际值: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                method,
                requestURI,
                e.getName(),
                e.getRequiredType().getSimpleName(),
                value,
                e.getMessage(),
                e);

        return AjaxResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'",
                e.getName(), e.getRequiredType().getSimpleName(), value));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String params = getRequestParams(request); // 获取请求参数

        log.error("[{}] 未知运行时异常 - 请求方法: {}, 请求地址: {}, 请求参数: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                method,
                requestURI,
                params,
                e.getMessage(),
                e);

        return AjaxResult.error("系统异常，请联系管理员");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String params = getRequestParams(request);

        log.error("[{}] 系统异常 - 请求方法: {}, 请求地址: {}, 请求参数: {}, 异常信息: {}",
                LocalDateTime.now().format(formatter),
                method,
                requestURI,
                params,
                e.getMessage(),
                e);

        return AjaxResult.error("系统异常，请联系管理员");
    }

    /**
     * 自定义验证异常(BindException)
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e)
    {
        // 收集所有验证错误
        List<String> errorMessages = e.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return String.format("字段[%s]: %s", fieldError.getField(), fieldError.getDefaultMessage());
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        log.error("[{}] 参数绑定异常 - 错误总数: {}, 错误详情: {}",
                LocalDateTime.now().format(formatter),
                errorMessages.size(),
                StringUtils.join(errorMessages, "; "),
                e);

        return AjaxResult.error(StringUtils.join(errorMessages, "; "));
    }

    /**
     * 自定义验证异常(MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        // 收集所有字段验证错误
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("字段[%s]: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        log.error("[{}] 参数验证异常 - 错误总数: {}, 错误详情: {}",
                LocalDateTime.now().format(formatter),
                errorMessages.size(),
                StringUtils.join(errorMessages, "; "),
                e);

        return AjaxResult.error(StringUtils.join(errorMessages, "; "));
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e)
    {
        log.warn("[{}] 演示模式操作拦截 - 异常信息: {}",
                LocalDateTime.now().format(formatter),
                e.getMessage());

        return AjaxResult.error("演示模式，不允许操作");
    }

    /**
     * 获取当前请求的参数信息
     */
    private String getRequestParams(HttpServletRequest request) {
        try {
            // 获取请求参数并转换为字符串
            return request.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + StringUtils.join(entry.getValue(), ","))
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            log.error("获取请求参数失败", e);
            return "获取参数失败";
        }
    }

    /**
     * 获取当前登录用户名（需要根据实际项目实现）
     */
    private String getCurrentUsername() {
        try {
            // 这里只是示例，实际项目中需要从SecurityContext或Session中获取
            // return SecurityUtils.getUsername();
            return "未获取到用户信息";
        } catch (Exception e) {
            return "获取用户失败";
        }
    }
}
