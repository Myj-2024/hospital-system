package com.hospital.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hospital.common.exception.BusinessException;
import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.DepartmentPageQuery;
import com.hospital.dto.user.DepartmentDTO;
import com.hospital.mapper.sys.DepartmentMapper;
import com.hospital.service.system.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 分页查询科室列表
     */
    @Override
    public PageResult pageQuery(DepartmentPageQuery departmentPageQuery) {
        PageHelper.startPage(departmentPageQuery.getPageNum(), departmentPageQuery.getPageSize());
        Page<DepartmentDTO> page = departmentMapper.pageQuery(departmentPageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增科室
     */
    @Override
    public boolean addDepartment(DepartmentDTO departmentDTO) {
        return departmentMapper.addDepartment(departmentDTO) > 0;
    }

    /**
     * 修改科室
     */
    @Override
    public boolean updateDepartment(DepartmentDTO departmentDTO) {
        return departmentMapper.updateDepartment(departmentDTO) > 0;
    }

    /**
     * 批量/单个删除科室
     */
    @Override
    public boolean deleteDepartment(DepartmentDTO departmentDTO) {
        List<Long> idList = departmentDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            // 补充：校验单个ID非空（原有逻辑缺失）
            if (departmentDTO.getId() == null) {
                throw new BusinessException("待删除的科室ID不能为空");
            }
            idList = List.of(departmentDTO.getId());
        }
        return departmentMapper.batchDeleteDepartment(idList) > 0;
    }

    /**
     * 根据ID查询科室
     */
    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        return departmentMapper.getDepartmentById(id);
    }

    /**
     * 修改科室状态
     */
    @Override
    public boolean updateDepartmentStatus(DepartmentDTO departmentDTO) {
        return departmentMapper.updateDepartment(departmentDTO) > 0;
    }
}
