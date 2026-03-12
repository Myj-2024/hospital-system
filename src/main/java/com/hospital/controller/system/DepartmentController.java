package com.hospital.controller.system;

import com.hospital.common.constant.MessageConstant;
import com.hospital.common.response.PageResult;
import com.hospital.common.response.Result;
import com.hospital.dto.pagequery.DepartmentPageQuery;
import com.hospital.dto.user.DepartmentDTO;
import com.hospital.service.system.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system/department")
@Slf4j
@Tag(name = "科室管理", description = "科室管理")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 分页查询科室列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询", description = "分页查询科室列表")
    public Result<PageResult> getDepartmentPage(DepartmentPageQuery departmentPageQuery) {
        log.info("分页查询医生列表：{}", departmentPageQuery);
        PageResult page = departmentService.pageQuery(departmentPageQuery);
        return Result.success(page);
    }

    /**
     * 新增科室
     */
    @PostMapping("/add")
    @Operation(summary = "新增科室", description = "新增科室")
    public Result<DepartmentDTO> addDepartment(@RequestBody DepartmentDTO departmentDTO){
        log.info("新增科室：{}", departmentDTO);
        boolean result = departmentService.addDepartment(departmentDTO);
        if (result) {
            return Result.success(departmentDTO);
        }
        return Result.sysError(MessageConstant.DEPARTMENT_INSERT_ERROR);
    }

    /**
     * 修改科室
     */
    @PutMapping("/update")
    @Operation(summary = "修改科室", description = "修改科室")
    public Result<DepartmentDTO> updateDepartment(@RequestBody DepartmentDTO departmentDTO){
        log.info("修改科室：{}", departmentDTO);
        boolean result = departmentService.updateDepartment(departmentDTO);
        if (result) {
            return Result.success(departmentDTO);
        }
        return Result.sysError(MessageConstant.DEPARTMENT_UPDATE_ERROR);
    }

    /**
     * 单个/批量删除科室
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除科室", description = "支持单个删除（传单个id）和批量删除（传id列表）")
    public Result<Boolean> deleteDepartment(@RequestBody DepartmentDTO departmentDTO){
        log.info("删除科室：{}", departmentDTO);
        boolean result = departmentService.deleteDepartment(departmentDTO);
        if (result) {
            return Result.success(true);
        }
        return Result.sysError(MessageConstant.DEPARTMENT_DELETE_ERROR);
    }

    /**
     * 根据ID查询科室
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询科室", description = "根据ID查询科室")
    public Result<DepartmentDTO> getDepartmentById(Long id) {
        log.info("查询科室ID：{}", id);
        DepartmentDTO departmentDTO = departmentService.getDepartmentById(id);
        return Result.success(departmentDTO);
    }

    /**
     * 修改科室状态
     */
    @PutMapping("/status")
    @Operation(summary = "修改状态", description = "修改科室状态")
    public Result<DepartmentDTO> updateDepartmentStatus(@RequestBody DepartmentDTO departmentDTO){
        log.info("修改科室状态：{}", departmentDTO);
        boolean result = departmentService.updateDepartmentStatus(departmentDTO);
        if (result) {
            return Result.success(departmentDTO);
        }
        return Result.sysError(MessageConstant.DEPARTMENT_STATUS_UPDATE_ERROR);
    }
}
