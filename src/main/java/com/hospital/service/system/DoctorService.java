package com.hospital.service.system;

import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.DoctorDTO; // 替换为医生专属DTO
import com.hospital.dto.user.UserStatusDTO;

public interface DoctorService {

    /**
     * 分页查询医生列表
     */
    PageResult pageQuery(UserPageQuery userPageQuery);

    /**
     * 根据id查询医生（关联sys_doctor）
     */
    DoctorDTO getDoctorById(Long id);

    /**
     * 新增医生（双表插入）
     */
    boolean addDoctor(DoctorDTO doctorDTO);

    /**
     * 修改医生（双表更新）
     */
    boolean updateDoctor(DoctorDTO doctorDTO);

    /**
     * 删除医生（删除sys_doctor + 禁用sys_user）
     */
    boolean deleteDoctor(DoctorDTO doctorDTO);

    /**
     * 修改医生状态
     */
    boolean updateDoctorStatus(UserStatusDTO statusDTO);
}