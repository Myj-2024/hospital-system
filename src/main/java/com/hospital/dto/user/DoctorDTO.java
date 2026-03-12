package com.hospital.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 医生专属DTO（继承通用用户DTO）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DoctorDTO extends UserDTO {
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
