package com.hospital.dto.pagequery;

import lombok.Data;

@Data
public class DepartmentPageQuery {

    private int pageNum;

    private int pageSize;

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
