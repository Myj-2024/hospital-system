package com.hospital.mapper.sys;

import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.DoctorDTO;
import com.hospital.dto.user.UserStatusDTO;
import com.hospital.entity.sys.SysDoctor;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DoctorMapper {

    // 分页查询（联表sys_user + sys_doctor）
    Page<DoctorDTO> pageQuery(UserPageQuery userPageQuery);

    // 根据ID查询医生（联表）
    DoctorDTO getDoctorById(Long id);

    // 批量查询医生
    List<DoctorDTO> batchGetDoctorByIds(@Param("idList") List<Long> idList);

    // 插入sys_user表
    boolean addUser(DoctorDTO doctorDTO);

    // 获取新增用户的自增ID
    @Select("SELECT LAST_INSERT_ID()")
    Long getLastInsertId();

    // 插入sys_doctor表
    boolean addDoctor(SysDoctor sysDoctor);

    // 更新sys_user表
    boolean updateUser(DoctorDTO doctorDTO);

    // 更新sys_doctor表
    boolean updateDoctor(SysDoctor sysDoctor);

    // 批量删除sys_doctor表
    int batchDeleteDoctor(@Param("idList") List<Long> idList);

    // 修改用户状态
    boolean updateUserStatus(UserStatusDTO statusDTO);

    // 批量修改用户状态
    int batchUpdateUserStatus(@Param("idList") List<Long> idList, @Param("status") Integer status);

    // 批量查询用户状态（保留原有方法）
    List<UserStatusDTO> batchGetUserStatusByIds(@Param("idList") List<Long> idList);

    // 查询单个用户状态（保留原有方法）
    Integer getUserStatusById(Long id);

    /**
     * 新增：批量物理删除sys_user表中的医生记录
     */
    int batchDeleteUserByIds(@Param("idList") List<Long> idList);
}