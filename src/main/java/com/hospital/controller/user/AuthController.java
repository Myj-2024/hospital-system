package com.hospital.controller.user;

import com.hospital.common.constant.MessageConstant;
import com.hospital.common.response.Result;
import com.hospital.common.utils.JwtUtil;
import com.hospital.dto.user.UserLoginDTO;
import com.hospital.service.user.UserService;
import com.hospital.common.exception.BusinessException;
import com.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/auth")
@Tag(name = "用户登录", description = "用户登录和Token刷新接口")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码获取登录凭证")
    public Result<UserLoginVO> login(
            @Parameter(description = "登录信息", required = true)
            @RequestBody UserLoginDTO userLoginDTO){
        try {
            // 1. 参数非空校验
            if (!StringUtils.hasText(userLoginDTO.getUsername()) ||
                    !StringUtils.hasText(userLoginDTO.getPassword())) {
                return Result.sysError(MessageConstant.PARAMETER_ERROR);
            }

            // 2. 验证码校验逻辑 (此处仅做非空检查，具体校验逻辑需根据你的验证码组件实现)
            if (!StringUtils.hasText(userLoginDTO.getCaptcha())) {
                return Result.sysError("验证码不能为空");
            }

            log.info("用户登录请求：{}", userLoginDTO.getUsername());
            UserLoginVO userLoginVO = userService.login(userLoginDTO);
            log.info("用户登录成功：{}", userLoginDTO.getUsername());
            return Result.success(userLoginVO);

        } catch (BusinessException e) {
            // 捕获预期的业务异常（如：用户名不存在，密码错误）
            log.warn("登录业务异常：{}", e.getMessage());
            return Result.sysError(e.getMessage());
        } catch (Exception e) {
            log.error("用户登录系统异常", e);
            return Result.sysError(MessageConstant.USER_LOGIN_ERROR);
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "使用有效的旧Token获取新Token")
    public Result<String> refreshToken(
            @Parameter(description = "Bearer Token", required = true)
            @RequestHeader("Authorization") String token){
        try {
            if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
                return Result.sysError("Token格式错误");
            }

            String realToken = token.substring(7).trim();
            if (jwtUtil.validateToken(realToken)) {
                String newToken = jwtUtil.refreshToken(realToken);
                return Result.success(newToken);
            } else {
                return Result.sysError("Token已过期");
            }
        } catch (Exception e) {
            log.error("Token刷新异常", e);
            return Result.sysError("刷新失败");
        }
    }
}