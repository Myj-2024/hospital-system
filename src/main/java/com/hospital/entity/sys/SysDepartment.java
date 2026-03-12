package com.hospital.entity.sys;

import com.hospital.entity.base.BaseEntity;
import lombok.Data;

@Data
public class SysDepartment extends BaseEntity {

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 科室描述
     */
    private String deptDesc;

    /**
     * 科室状态：0-禁用，1-正常
     */
    private Integer status;
}
