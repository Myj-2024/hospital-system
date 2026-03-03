package com.hospital.controller.user;


import cn.hutool.captcha.LineCaptcha;
import com.hospital.common.response.Result;
import com.hospital.common.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping
    public Result<Map<String, String>> getCaptcha() {

        LineCaptcha captcha = new LineCaptcha(120, 40, 4, 20);
        String code = captcha.getCode();

        String key = UUID.randomUUID().toString();

        // 存Redis，5分钟过期
        redisUtil.set("captcha:" + key, code, 300);

        Map<String, String> data = new HashMap<>();
        data.put("key", key);
        data.put("image", captcha.getImageBase64());

        return Result.success(data);
    }
}