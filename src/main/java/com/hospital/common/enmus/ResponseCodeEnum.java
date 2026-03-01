package com.hospital.common.enmus;

import lombok.Getter;

/**
 * 业务状态码枚举
 * 编码规范：
 * - 通用码：0（成功）、-1（系统异常）
 * - 预约相关：4001-4099
 * - 缴费相关：4101-4199
 * - 用户相关：4201-4299
 * - 权限相关：4301-4399
 * - 科室相关：4401-4499
 * - 医生相关：4501-4599
 * - 诊疗相关：4601-4699
 * - 排队相关：4701-4799
 * - 参数校验：4801-4899
 */
@Getter
public enum ResponseCodeEnum {
    // ===================== 通用状态码 =====================
    SUCCESS(0, "操作成功"),
    SYSTEM_ERROR(-1, "系统异常，请稍后重试"),
    PARAM_ERROR(-2, "参数格式错误"),
    BUSINESS_ERROR(-3, "业务异常" ),

    // ===================== 预约相关（4001-4099） =====================
    APPOINTMENT_NO_SOURCE(4001, "号源不足，预约失败"),
    APPOINTMENT_LOCK_FAILED(4002, "预约请求过忙，请稍后再试"),
    APPOINTMENT_CANCEL_FAILED(4003, "预约已开始，无法取消"),
    APPOINTMENT_NOT_EXIST(4004, "预约记录不存在"),
    APPOINTMENT_STATUS_ERROR(4005, "预约状态异常，无法操作"),
    APPOINTMENT_TIME_INVALID(4006, "预约时间不合法（非医生出诊时间）"),
    APPOINTMENT_REPEAT(4007, "同一时段已预约该医生，请勿重复预约"),
    APPOINTMENT_PAST_TIME(4008, "预约时间已过期，无法预约"),

    // ===================== 缴费相关（4101-4199） =====================
    PAYMENT_BALANCE_NOT_ENOUGH(4101, "账户余额不足"),
    PAYMENT_ORDER_EXPIRED(4102, "订单已过期"),
    PAYMENT_NOT_EXIST(4103, "缴费订单不存在"),
    PAYMENT_STATUS_ERROR(4104, "订单状态异常，无法支付"),
    PAYMENT_AMOUNT_ERROR(4105, "缴费金额错误"),
    PAYMENT_REPEAT(4106, "该订单已支付，请勿重复缴费"),
    PAYMENT_TYPE_ERROR(4107, "支付方式不支持"),

    // ===================== 用户相关（4201-4299） =====================
    USER_NOT_EXIST(4201, "用户不存在"),
    USER_STATUS_DISABLED(4202, "用户已禁用"),
    USER_PASSWORD_ERROR(4203, "密码错误"),
    USER_NAME_DUPLICATE(4204, "用户名已存在"),
    USER_PHONE_DUPLICATE(4205, "手机号已绑定其他账户"),
    USER_ID_CARD_ERROR(4206, "身份证号格式错误"),
    USER_NOT_LOGIN(4207, "用户未登录，请先登录"),

    // ===================== 权限相关（4301-4399） =====================
    PERMISSION_DENIED(4301, "权限不足，无法操作"),
    TOKEN_EXPIRED(4302, "登录已过期，请重新登录"),
    TOKEN_INVALID(4303, "令牌无效，请重新登录"),
    ROLE_NOT_EXIST(4304, "角色不存在"),
    MENU_NOT_EXIST(4305, "菜单权限不存在"),

    // ===================== 科室相关（4401-4499） =====================
    DEPT_NOT_EXIST(4401, "科室不存在"),
    DEPT_NAME_DUPLICATE(4402, "科室名称已存在"),
    DEPT_STATUS_DISABLED(4403, "科室已禁用，无法操作"),
    DEPT_HAS_DOCTOR(4404, "科室下仍有医生，无法删除"),

    // ===================== 医生相关（4501-4599） =====================
    DOCTOR_NOT_EXIST(4501, "医生不存在"),
    DOCTOR_USER_DUPLICATE(4502, "该用户已绑定医生信息，无法重复绑定"),
    DOCTOR_STATUS_DISABLED(4503, "医生已禁用，无法预约"),
    DOCTOR_NO_WORK_TIME(4504, "医生暂无出诊时间"),
    DOCTOR_DEPT_ERROR(4505, "医生所属科室不存在"),
    DOCTOR_TITLE_ERROR(4506, "医生职称格式错误"),

    // ===================== 诊疗相关（4601-4699） =====================
    DIAGNOSIS_NOT_EXIST(4601, "诊疗记录不存在"),
    DIAGNOSIS_STATUS_ERROR(4602, "诊疗状态异常，无法操作"),
    DIAGNOSIS_APPOINTMENT_ERROR(4603, "未找到关联的预约记录"),
    PRESCRIPTION_FORMAT_ERROR(4604, "处方格式错误（JSON解析失败）"),

    // ===================== 排队相关（4701-4799） =====================
    QUEUE_NOT_EXIST(4701, "排队记录不存在"),
    QUEUE_NUMBER_DUPLICATE(4702, "同一医生排队号重复"),
    QUEUE_STATUS_ERROR(4703, "排队状态异常，无法叫号"),
    QUEUE_PAST_NUMBER(4704, "排队号已过号"),

    // ===================== 参数校验（4801-4899） =====================
    PARAM_EMPTY(4801, "必填参数为空"),
    PARAM_PHONE_ERROR(4802, "手机号格式错误"),
    PARAM_EMAIL_ERROR(4803, "邮箱格式错误"),
    PARAM_DATE_ERROR(4804, "日期格式错误"),
    PARAM_NUMBER_ERROR(4805, "数字格式错误");


    private final int bizCode;
    private final String msg;

    ResponseCodeEnum(int bizCode, String msg) {
        this.bizCode = bizCode;
        this.msg = msg;
    }

    // 扩展：根据状态码获取枚举（便于全局异常/拦截器使用）
    public static ResponseCodeEnum getByBizCode(int bizCode) {
        for (ResponseCodeEnum enumItem : values()) {
            if (enumItem.getBizCode() == bizCode) {
                return enumItem;
            }
        }
        return SYSTEM_ERROR; // 未匹配到返回系统异常
    }
}