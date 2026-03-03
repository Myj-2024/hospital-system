package com.hospital.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "用户登录参数", name = "UserLoginDTO")
public class UserLoginDTO implements Serializable {

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "用户密码")
    private String password;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "验证码唯一标识key")
    private String captchaKey;
}