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
    private Long departmentId;

    /**
     * 职称
     */
    private String title;

    /**
     * 擅长领域
     */
    private String skill;
}
