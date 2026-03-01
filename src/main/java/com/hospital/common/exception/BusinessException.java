package com.hospital.common.exception;

import com.hospital.common.enmus.ResponseCodeEnum;
import lombok.Getter;

/**
 * 业务异常类（适配ResponseCodeEnum）
 * 用于抛出所有业务层面的异常（如参数错误、数据不存在、重复提交等）
 */
@Getter // 提供字段的getter方法，供全局异常处理器获取
public class BusinessException extends RuntimeException {

    /**
     * 业务状态码（对应ResponseCodeEnum的bizCode）
     */
    private Integer bizCode;

    /**
     * 异常信息
     */
    private String message;

    // ===================== 构造方法 =====================

    /**
     * 仅传递异常信息（默认使用通用业务错误码）
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
        this.bizCode = ResponseCodeEnum.BUSINESS_ERROR.getBizCode(); // 通用业务错误码
        this.message = message;
    }

    /**
     * 传递状态码+异常信息（自定义码）
     * @param bizCode 业务状态码
     * @param message 异常信息
     */
    public BusinessException(Integer bizCode, String message) {
        super(message);
        this.bizCode = bizCode;
        this.message = message;
    }

    /**
     * 直接传递枚举（推荐使用，统一管理码值）
     * @param responseCodeEnum 响应码枚举
     */
    public BusinessException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMsg());
        this.bizCode = responseCodeEnum.getBizCode();
        this.message = responseCodeEnum.getMsg();
    }

    /**
     * 枚举+自定义信息（适配需要拼接动态信息的场景）
     * @param responseCodeEnum 响应码枚举
     * @param customMessage 自定义异常信息
     */
    public BusinessException(ResponseCodeEnum responseCodeEnum, String customMessage) {
        super(customMessage);
        this.bizCode = responseCodeEnum.getBizCode();
        this.message = customMessage;
    }

    /**
     * 带根因的异常（便于排查问题）
     * @param responseCodeEnum 响应码枚举
     * @param cause 根异常
     */
    public BusinessException(ResponseCodeEnum responseCodeEnum, Throwable cause) {
        super(responseCodeEnum.getMsg(), cause);
        this.bizCode = responseCodeEnum.getBizCode();
        this.message = responseCodeEnum.getMsg();
    }
}