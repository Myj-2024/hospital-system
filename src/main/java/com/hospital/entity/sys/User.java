package com.hospital.entity.sys;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号（可做唯一校验）
     */
    private String phone = "";

    /**
     * 身份证号
     */
    private String identityCard = "";

    /**
     * 真实姓名
     */
    private String realName = "";

    /**
     * 角色Id
     */
    private Integer roleId;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
