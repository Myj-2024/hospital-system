package com.hospital.controller.system;


import com.hospital.common.constant.MessageConstant;
import com.hospital.common.response.PageResult;
import com.hospital.common.response.Result;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.NurseDTO;
import com.hospital.service.system.NurseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system/nurse")
@Slf4j
public class NurseController {

    @Autowired
    private NurseService nurseService;

    /**
     * 分页查询护士列表（自动过滤role_id=3）
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询", description = "分页查询护士列表")
    public Result<PageResult> getNursePage(UserPageQuery userPageQuery) {
        log.info("分页查询护士列表：{}", userPageQuery);
        userPageQuery.setRoleId(3);
        PageResult page = nurseService.pageQuery(userPageQuery);
        return Result.success(page);
    }

    /**
     * 根据id查询护士（关联sys_nurse表）
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询护士", description = "根据id查询护士")
    public Result<NurseDTO> getNurseById(@PathVariable Long id) {
        log.info("根据id查询护士：{}", id);
        NurseDTO nurseDTO = nurseService.getNurseById(id);
        return Result.success(nurseDTO);
    }

    /**
     * 新增护士（同时插入sys_user和sys_nurse）
     */
    @PostMapping("/add")
    @Operation(summary = "新增护士", description = "新增护士")
    public Result<NurseDTO> addNurse(@RequestBody NurseDTO nurseDTO) {
        log.info("新增护士：{}", nurseDTO);
        // 强制设置角色为护士
        nurseDTO.setRoleId(3);
        boolean result = nurseService.addNurse(nurseDTO);
        if (result) {
            return Result.success(nurseDTO);
        }
        return Result.sysError(MessageConstant.NURSE_INSERT_ERROR);
    }

    /**
     * 修改护士（同时更新sys_user和sys_nurse）
     */
    @PutMapping("/update")
    @Operation(summary = "修改护士", description = "修改护士")
    public Result<NurseDTO> updateNurse(@RequestBody NurseDTO nurseDTO) {
        log.info("修改护士：{}", nurseDTO);
        boolean result = nurseService.updateNurse(nurseDTO);
        if (result) {
            return Result.success(nurseDTO);
        }
        return Result.sysError(MessageConstant.NURSE_UPDATE_ERROR);
    }

    /**
     * 单个/批量删除护士（仅删除sys_nurse，逻辑禁用sys_user）
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除护士", description = "支持单个删除（传单个id）和批量删除（传id列表）")
    public Result<Boolean> deleteNurse(@RequestBody NurseDTO nurseDTO) {
        log.info("删除护士：{}", nurseDTO);
        boolean result = nurseService.deleteNurse(nurseDTO);
        if (result) {
            return Result.success(true);
        }
        return Result.sysError(MessageConstant.NURSE_DELETE_STAUS_ERROR);
    }

    /**
     * 修改护士状态
     */
    @PostMapping("/status")
    @Operation(summary = "修改状态", description = "仅修改用户状态，不删除数据")
    public Result<Boolean> updateStatus(@RequestBody NurseDTO statusDTO) {
        log.info("修改护士状态：{}", statusDTO);
        boolean result = nurseService.updateNurseStatus(statusDTO);
        if (result) {
            return Result.success(true);
        }
        return Result.sysError(MessageConstant.NURSE_UPDATE_ERROR);
    }
}
