package com.hospital.entity.sys;

import com.hospital.entity.base.BaseEntity;
import lombok.Data;

/**
 * 医生专属表实体
 */
@Data
public class SysDoctor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 关联sys_user的主键
     */
    private Long userId;

    /**
     * 科室ID
     */
    private Long deptId;


    /**
     * 职称
     */
    private String title;

    /**
     * 擅长领域
     */
    private String specialty;

    /**
     * 出诊时间
     */
    private String workTime;

    /**
     * 每日最大接诊数
     */
    private Long dailyMaxNum;

    /**
     * 科室名称
     */
    private String deptName;
}