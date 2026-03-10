package com.hospital.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hospital.common.exception.BusinessException;
import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.DoctorDTO;
import com.hospital.dto.user.UserStatusDTO;
import com.hospital.entity.sys.SysDoctor;
import com.hospital.mapper.sys.DoctorMapper;
import com.hospital.service.system.DoctorService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 分页查询医生（联表sys_user + sys_doctor）
     */
    @Override
    public PageResult pageQuery(UserPageQuery userPageQuery) {
        // 强制校验角色ID，防止传入其他角色
        if (userPageQuery.getRoleId() == null || userPageQuery.getRoleId() != 2) {
            userPageQuery.setRoleId(2);
        }
        PageHelper.startPage(userPageQuery.getPageNum(), userPageQuery.getPageSize());
        Page<DoctorDTO> page = doctorMapper.pageQuery(userPageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据ID查询医生（联表查询）
     */
    @Override
    public DoctorDTO getDoctorById(Long id) {
        // 1. 查询医生基础信息 + 专属信息
        DoctorDTO doctorDTO = doctorMapper.getDoctorById(id);
        if (doctorDTO == null) {
            throw new BusinessException("医生不存在");
        }
        // 2. 校验是否为医生角色
        if (doctorDTO.getRoleId() != 2) {
            throw new BusinessException("该用户不是医生，无法查询");
        }
        return doctorDTO;
    }

    /**
     * 新增医生（事务保证双表插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDoctor(DoctorDTO doctorDTO) {
        // 1. 插入sys_user表
        boolean userResult = doctorMapper.addUser(doctorDTO);
        if (!userResult) {
            throw new BusinessException("新增用户失败");
        }
        // 2. 获取自增的userId，插入sys_doctor表
        Long userId = doctorMapper.getLastInsertId();
        SysDoctor sysDoctor = new SysDoctor();
        BeanUtils.copyProperties(doctorDTO, sysDoctor);
        sysDoctor.setUserId(userId);
        boolean doctorResult = doctorMapper.addDoctor(sysDoctor);
        if (!doctorResult) {
            throw new BusinessException("新增医生信息失败");
        }
        return true;
    }

    /**
     * 修改医生（事务保证双表更新）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDoctor(DoctorDTO doctorDTO) {
        // 1. 校验是否为医生
        DoctorDTO existDoctor = doctorMapper.getDoctorById(doctorDTO.getId());
        if (existDoctor == null || existDoctor.getRoleId() != 2) {
            throw new BusinessException("只能修改医生信息");
        }
        // 2. 更新sys_user表
        boolean userResult = doctorMapper.updateUser(doctorDTO);
        // 3. 更新sys_doctor表
        SysDoctor sysDoctor = new SysDoctor();
        BeanUtils.copyProperties(doctorDTO, sysDoctor);
        sysDoctor.setUserId(doctorDTO.getId());
        boolean doctorResult = doctorMapper.updateDoctor(sysDoctor);
        return userResult && doctorResult;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDoctor(DoctorDTO doctorDTO) {
        List<Long> idList = doctorDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            // 补充：校验单个ID非空（原有逻辑缺失）
            if (doctorDTO.getId() == null) {
                throw new BusinessException("待删除的医生ID不能为空");
            }
            idList = List.of(doctorDTO.getId());
        }

        // 1. 校验所有ID是否为医生角色
        List<DoctorDTO> doctorList = doctorMapper.batchGetDoctorByIds(idList);
        if (CollectionUtils.isEmpty(doctorList)) {
            throw new BusinessException("医生不存在");
        }
        List<Long> nonDoctorIds = doctorList.stream()
                .filter(dto -> dto.getRoleId() != 2)
                .map(DoctorDTO::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(nonDoctorIds)) {
            throw new BusinessException("只能删除医生账号，非医生ID：" + nonDoctorIds);
        }

        // 2. 批量物理删除sys_doctor表记录
        int doctorDeleteCount = doctorMapper.batchDeleteDoctor(idList);
        // 3. 批量物理删除sys_user表记录（替换原禁用逻辑）
        int userDeleteCount = doctorMapper.batchDeleteUserByIds(idList);

        // 校验操作结果：确保双表都删除成功
        if (doctorDeleteCount != idList.size() || userDeleteCount != idList.size()) {
            throw new BusinessException("医生删除操作部分失败，已触发事务回滚");
        }

        return true;
    }
    /**
     * 修改医生状态
     */
    @Override
    public boolean updateDoctorStatus(UserStatusDTO statusDTO) {
        if (statusDTO.getId() == null || statusDTO.getStatus() == null) {
            throw new BusinessException("医生ID和状态不能为空");
        }
        // 校验是否为医生
        DoctorDTO doctorDTO = doctorMapper.getDoctorById(statusDTO.getId());
        if (doctorDTO == null || doctorDTO.getRoleId() != 2) {
            throw new BusinessException("只能修改医生状态");
        }
        return doctorMapper.updateUserStatus(statusDTO);
    }
}