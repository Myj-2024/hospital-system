package com.hospital.entity.sys;

import com.hospital.entity.base.BaseEntity;
import lombok.Data;

@Data
public class SysNurse extends BaseEntity {

    /**
     * 关联sys_user的主键
     */
    private Long userId;

    /**
     * 病区ID
     */
    private Long wardId;

    /**
     * 护士等级
     */
    private String nurseLevel;
}
