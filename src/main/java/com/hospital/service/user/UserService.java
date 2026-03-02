package com.hospital.service.user;

import com.hospital.dto.user.UserLoginDTO;
import com.vo.UserLoginVO;

public interface UserService {
    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 用户登录结果（包含JWT Token）
     */
    UserLoginVO login(UserLoginDTO userLoginDTO);
}
