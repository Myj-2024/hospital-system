package com.hospital.mapper.sys;

import com.github.pagehelper.Page;
import com.hospital.dto.pagequery.DepartmentPageQuery;
import com.hospital.dto.user.DepartmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    /**
     * 分页查询科室列表
     */
    Page<DepartmentDTO> pageQuery(DepartmentPageQuery departmentPageQuery);

    /**
     * 新增科室
     */
    int addDepartment(DepartmentDTO departmentDTO);

    /**
     * 修改科室
     */
    int updateDepartment(DepartmentDTO departmentDTO);

    /**
     * 批量删除科室
     */
    int batchDeleteDepartment(List<Long> idList);

    /**
     * 根据ID查询科室
     */
    DepartmentDTO getDepartmentById(Long id);
}
