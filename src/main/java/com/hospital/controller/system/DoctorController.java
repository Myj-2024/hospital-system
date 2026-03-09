package com.hospital.controller.system;

import com.hospital.common.constant.MessageConstant;
import com.hospital.common.response.PageResult;
import com.hospital.common.response.Result;
import com.hospital.dto.pagequery.UserPageQuery;
import com.hospital.dto.user.UserDTO;
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
     * 分页查询用户列表
     *
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询", description = "分页查询用户列表")
    public Result<PageResult> getUserPage(UserPageQuery userPageQuery) {
        log.info("分页查询用户列表：{}", userPageQuery);
        PageResult page =doctorService.pageQuery(userPageQuery);
        PageResult pageResult = new PageResult(page.getTotal(), page.getRecords());
        return Result.success(pageResult);
    }

    /**
     * 新增 用户
     */
    @PostMapping("/add")
    @Operation(summary = "新增用户", description = "新增用户")
    public Result<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        log.info("新增用户：{}", userDTO);
        boolean result = doctorService.addUser(userDTO);
        if (result) {
            return Result.success(userDTO);
        }
        return Result.sysError(MessageConstant.USER_INSERT_ERROR);
    }
}
