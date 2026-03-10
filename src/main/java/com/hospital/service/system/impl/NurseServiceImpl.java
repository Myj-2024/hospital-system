package com.hospital.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hospital.common.constant.MessageConstant;
import com.hospital.common.exception.BusinessException;
import com.hospital.common.response.PageResult;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.NurseDTO;
import com.hospital.dto.user.UserStatusDTO;
import com.hospital.entity.sys.SysNurse;
import com.hospital.mapper.sys.NurseMapper;
import com.hospital.service.system.NurseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NurseServiceImpl implements NurseService {

    @Autowired
    private NurseMapper nurseMapper;

    // 统一常量管理（建议抽成枚举类，如 RoleEnum/NurseStatusEnum）
    private static final Integer NURSE_ROLE_ID = 3;       // 护士角色ID
    private static final Integer USER_STATUS_DISABLE = 0; // 用户禁用状态
    private static final Integer DELETE_STATUS = 1;       // 逻辑删除状态（根据你的业务调整）

    /**
     * 分页查询护士列表（强制过滤护士角色）
     */
    @Override
    public PageResult pageQuery(UserPageQuery userPageQuery) {
        // 强制设置为护士角色，避免查询其他角色
        userPageQuery.setRoleId(NURSE_ROLE_ID);
        PageHelper.startPage(userPageQuery.getPageNum(), userPageQuery.getPageSize());
        Page<NurseDTO> page = nurseMapper.pageQuery(userPageQuery);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询护士（关联sys_nurse表）
     */
    @Override
    public NurseDTO getNurseById(Long id) {
        if (id == null) {
            throw new BusinessException("护士ID不能为空");
        }
        // 1. 查询护士基础信息 + 专属信息
        NurseDTO nurseDTO = nurseMapper.getNurseById(id);
        if (nurseDTO == null) {
            throw new BusinessException("护士不存在");
        }
        // 2. 校验是否为护士角色
        if (!NURSE_ROLE_ID.equals(nurseDTO.getRoleId())) {
            throw new BusinessException("该用户不是护士，无法查询");
        }
        return nurseDTO;
    }

    /**
     * 新增护士（事务保证双表插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addNurse(NurseDTO nurseDTO) {
        // 强制设置护士角色
        nurseDTO.setRoleId(NURSE_ROLE_ID);
        // 1. 插入sys_user表
        boolean userResult = nurseMapper.addUser(nurseDTO);
        if (!userResult) {
            throw new BusinessException("新增用户失败");
        }
        // 2. 获取自增的userId，插入sys_nurse表
        Long userId = nurseMapper.getLastInsertId();
        if (userId == null) {
            throw new BusinessException("获取用户ID失败");
        }
        SysNurse sysNurse = new SysNurse();
        BeanUtils.copyProperties(nurseDTO, sysNurse);
        sysNurse.setUserId(userId); // 此处userId是sys_user的自增ID，不是护士ID
        boolean nurseResult = nurseMapper.addNurse(sysNurse);
        if (!nurseResult) {
            throw new BusinessException("新增护士信息失败");
        }
        return true;
    }

    /**
     * 修改护士（事务保证双表修改）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNurse(NurseDTO nurseDTO) {
        if (nurseDTO.getId() == null) {
            throw new BusinessException("护士ID不能为空");
        }
        // 1. 校验是否为护士
        NurseDTO existNurse = nurseMapper.getNurseById(nurseDTO.getId());
        if (existNurse == null || !NURSE_ROLE_ID.equals(existNurse.getRoleId())) {
            throw new BusinessException("只能修改护士信息");
        }
        // 2. 更新sys_user表记录
        boolean userResult = nurseMapper.updateUser(nurseDTO);
        // 3. 更新sys_nurse表记录（userId取原有值，避免覆盖）
        SysNurse sysNurse = new SysNurse();
        BeanUtils.copyProperties(nurseDTO, sysNurse);
        sysNurse.setUserId(existNurse.getId()); // 修正：取原有userId，而非传入的ID
        boolean nurseResult = nurseMapper.updateNurse(sysNurse);

        if (!userResult || !nurseResult) {
            throw new BusinessException("修改护士信息失败");
        }
        return true;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNurse(NurseDTO nurseDTO) {
        // 1. 处理入参：优先取idList，若无则取单个id，均为空则抛异常
        List<Long> idList = nurseDTO.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            Long singleId = nurseDTO.getId();
            if (singleId == null) {
                throw new BusinessException("待删除的护士ID不能为空");
            }
            idList = List.of(singleId);
        }

        // 2. 校验：查询所有ID对应的护士信息，校验ID是否存在且均为护士角色
        List<NurseDTO> nurseList = nurseMapper.batchGetNurseByIds(idList);
        if (CollectionUtils.isEmpty(nurseList)) {
            throw new BusinessException("待删除的护士不存在，ID列表：" + idList);
        }
        List<Long> existNurseIds = nurseList.stream()
                .map(NurseDTO::getId)
                .collect(Collectors.toList());
        List<Long> nonExistIds = idList.stream()
                .filter(id -> !existNurseIds.contains(id))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(nonExistIds)) {
            throw new BusinessException("以下ID对应的护士不存在：" + nonExistIds);
        }
        List<Long> nonNurseIds = nurseList.stream()
                .filter(dto -> !NURSE_ROLE_ID.equals(dto.getRoleId()))
                .map(NurseDTO::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(nonNurseIds)) {
            throw new BusinessException("只能删除护士账号，非护士ID：" + nonNurseIds);
        }

        // 3. 执行核心操作：双表物理删除（替换原禁用逻辑）
        // 3.1 批量物理删除sys_nurse表记录（移除无用的status参数）
        int nurseDeleteCount = nurseMapper.batchDeleteNurse(idList);
        // 3.2 批量物理删除sys_user表记录（替换原禁用逻辑）
        int userDeleteCount = nurseMapper.batchDeleteUserByIds(idList);

        // 4. 校验操作结果：确保双表都删除成功
        if (nurseDeleteCount != idList.size() || userDeleteCount != idList.size()) {
            throw new BusinessException("护士删除操作部分失败，已触发事务回滚");
        }

        return true;
    }
    /**
     * 修改护士状态
     */
    @Override
    public boolean updateNurseStatus(NurseDTO statusDTO) {
        if (statusDTO.getId() == null || statusDTO.getStatus() == null) {
            throw new BusinessException("护士ID和状态不能为空");
        }
        NurseDTO nurseDTO = nurseMapper.getNurseById(statusDTO.getId());
        if (nurseDTO == null || !NURSE_ROLE_ID.equals(nurseDTO.getRoleId())) {
            throw new BusinessException("只能修改护士状态");
        }
        return nurseMapper.updateUserStatus(statusDTO);
    }
}