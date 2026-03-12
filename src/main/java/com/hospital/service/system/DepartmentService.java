package com.hospital.service.system;

import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.DepartmentPageQuery;
import com.hospital.dto.user.DepartmentDTO;

public interface DepartmentService {

    /**
     * 分页查询科室列表
     */
    PageResult pageQuery(DepartmentPageQuery departmentPageQuery);

    /**
     * 新增科室
     */
    boolean addDepartment(DepartmentDTO departmentDTO);

    /**
     * 修改科室
     */
    boolean updateDepartment(DepartmentDTO departmentDTO);

    /**
     * 删除科室
     */
    boolean deleteDepartment(DepartmentDTO departmentDTO);

    /**
     * 根据ID查询科室
     */
    DepartmentDTO getDepartmentById(Long id);

    /**
     * 修改科室状态
     */
    boolean updateDepartmentStatus(DepartmentDTO departmentDTO);
}
