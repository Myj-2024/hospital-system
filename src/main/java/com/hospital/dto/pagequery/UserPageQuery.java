package com.hospital.dto.pagequery;

import com.hospital.entity.base.BaseEntity;
import lombok.Data;


@Data
public class UserPageQuery extends BaseEntity {

    private int pageNum;

    private int pageSize;

    /**
     * 用户名
     */
    private String username;

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
}
