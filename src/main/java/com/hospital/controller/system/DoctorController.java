package com.hospital.controller.system;

import com.hospital.common.constant.MessageConstant;
import com.hospital.common.response.PageResult;
import com.hospital.common.response.Result;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.DoctorDTO;
import com.hospital.dto.user.UserStatusDTO;
import com.hospital.service.system.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system/doctor")
@Slf4j
@Tag(name = "医生管理", description = "医生管理")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 分页查询医生列表（自动过滤role_id=2）
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询", description = "分页查询医生列表")
    public Result<PageResult> getDoctorPage(UserPageQuery userPageQuery) {
        log.info("分页查询医生列表：{}", userPageQuery);
        userPageQuery.setRoleId(2);
        PageResult page = doctorService.pageQuery(userPageQuery);
        return Result.success(page);
    }

    /**
     * 根据id查询医生（关联sys_doctor表）
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询医生", description = "根据id查询医生")
    public Result<DoctorDTO> getDoctorById(@PathVariable Long id) {
        log.info("根据id查询医生：{}", id);
        DoctorDTO doctorDTO = doctorService.getDoctorById(id);
        return Result.success(doctorDTO);
    }

    /**
     * 新增医生（同时插入sys_user和sys_doctor）
     */
    @PostMapping("/add")
    @Operation(summary = "新增医生", description = "新增医生")
    public Result<DoctorDTO> addDoctor(@RequestBody DoctorDTO doctorDTO) {
        log.info("新增医生：{}", doctorDTO);
        // 强制设置角色为医生
        doctorDTO.setRoleId(2);
        boolean result = doctorService.addDoctor(doctorDTO);
        if (result) {
            return Result.success(doctorDTO);
        }
        return Result.sysError(MessageConstant.DOCTOR_INSERT_ERROR);
    }

    /**
     * 修改医生（同时更新sys_user和sys_doctor）
     */
    @PutMapping("/update")
    @Operation(summary = "修改医生", description = "修改医生")
    public Result<DoctorDTO> updateDoctor(@RequestBody DoctorDTO doctorDTO) {
        log.info("修改医生：{}", doctorDTO);
        boolean result = doctorService.updateDoctor(doctorDTO);
        if (result) {
            return Result.success(doctorDTO);
        }
        return Result.sysError(MessageConstant.DOCTOR_UPDATE_ERROR);
    }

    /**
     * 单个/批量删除医生（仅删除sys_doctor，逻辑禁用sys_user）
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除医生", description = "支持单个删除（传单个id）和批量删除（传id列表）")
    public Result<Boolean> deleteDoctor(@RequestBody DoctorDTO doctorDTO) { // 改为DoctorDTO
        log.info("删除医生：{}", doctorDTO);
        boolean result = doctorService.deleteDoctor(doctorDTO);
        if (result) {
            return Result.success(true);
        }
        return Result.sysError(MessageConstant.DOCTOR_DELETE_STAUS_ERROR);
    }

    /**
     * 修改医生状态
     */
    @PostMapping("/status")
    @Operation(summary = "修改状态", description = "仅修改用户状态，不删除数据")
    public Result<Boolean> updateStatus(@RequestBody UserStatusDTO statusDTO) {
        log.info("修改医生状态：{}", statusDTO);
        boolean result = doctorService.updateDoctorStatus(statusDTO);
        if (result) {
            return Result.success(true);
        }
        return Result.sysError(MessageConstant.USER_UPDATE_ERROR);
    }
}