package com.hospital.service.system;

import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.NurseDTO;

public interface NurseService {

    /**
     * 分页查询护士列表（自动过滤role_id=3）
     */
    PageResult pageQuery(UserPageQuery userPageQuery);

    /**
     * 根据id查询护士（关联sys_nurse表）
     */
    NurseDTO getNurseById(Long id);

    /**
     * 新增护士（同时插入sys_user和sys_nurse）
     */
    boolean addNurse(NurseDTO nurseDTO);

    /**
     * 修改护士（同时更新sys_user和sys_nurse）
     */
    boolean updateNurse(NurseDTO nurseDTO);

    /**
     * 单个/批量删除护士（仅删除sys_nurse，逻辑禁用sys_user）
     */
    boolean deleteNurse(NurseDTO nurseDTO);

    /**
     * 修改护士状态
     */
    boolean updateNurseStatus(NurseDTO statusDTO);
}
