package com.hospital.dto.user;

import com.hospital.entity.base.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentDTO extends BaseEntity {

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

    // 新增：用于接收批量删除的ID列表
    private List<Long> idList;
}
