package com.hospital.common.exception;


import com.hospital.common.enmus.ResponseCodeEnum;
import com.hospital.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（增强版）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.bizError(e.getBizCode(), e.getMessage());
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleSystemException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.sysError(ResponseCodeEnum.SYSTEM_ERROR.getMsg());
    }

    /**
     * 处理权限异常
     */
    @ExceptionHandler(SecurityException.class)
    public Result<?> handleSecurityException(SecurityException e) {
        log.error("权限异常：{}", e.getMessage(), e);
        return Result.bizError(ResponseCodeEnum.PERMISSION_DENIED.getBizCode(), ResponseCodeEnum.PERMISSION_DENIED.getMsg());
    }
}
