package com.hospital.dto.user;

import lombok.Data;

@Data
public class NurseDTO extends UserDTO{

    /**
     * 病区ID
     */
    private Long wardId;

    /**
     * 护士等级
     */
    private String nurseLevel;
}
