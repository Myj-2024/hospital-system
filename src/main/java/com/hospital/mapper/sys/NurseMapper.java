package com.hospital.mapper.sys;

import com.github.pagehelper.Page;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.NurseDTO;
import com.hospital.entity.sys.SysNurse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NurseMapper {

    /**
     * 分页查询护士列表（自动过滤role_id=3）
     */
    Page<NurseDTO> pageQuery(UserPageQuery userPageQuery);

    /**
     * 根据id查询护士（关联sys_nurse表）
     */
    NurseDTO getNurseById(Long id);

    /**
     * 新增用户到sys_user表
     */
    boolean addUser(NurseDTO nurseDTO);

    /**
     * 获取sys_user表自增ID
     */
    @Select("SELECT LAST_INSERT_ID()")
    Long getLastInsertId();

    /**
     * 新增护士到sys_nurse表
     */
    boolean addNurse(SysNurse sysNurse);

    /**
     * 更新sys_user表护士基础信息
     */
    boolean updateUser(NurseDTO nurseDTO);

    /**
     * 更新sys_nurse表护士专属信息
     */
    boolean updateNurse(SysNurse sysNurse);

    /**
     * 批量查询护士（关联sys_nurse表）
     */
    List<NurseDTO> batchGetNurseByIds(@Param("idList") List<Long> idList);

    /**
     * 批量禁用sys_user表中的护士账号
     * @param idList 待禁用的用户ID列表
     * @param status 目标状态（0=禁用，1=启用）
     * @return 受影响行数
     */
    int batchUpdateUserStatus(@Param("idList") List<Long> idList, @Param("status") Integer status);

    /**
     * 修改单个护士的状态
     */
    boolean updateUserStatus(NurseDTO statusDTO);

    /**
     * 批量删除/逻辑删除sys_nurse表护士记录（移除无用的status参数）
     */
    int batchDeleteNurse(@Param("idList") List<Long> idList);

    /**
     * 新增：批量物理删除sys_user表中的护士记录
     */
    int batchDeleteUserByIds(@Param("idList") List<Long> idList);
}