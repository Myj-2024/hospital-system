package com.hospital.service.user.impl;

import com.hospital.common.exception.BusinessException;
import com.hospital.common.utils.JwtUtil;
import com.hospital.dto.user.UserLoginDTO;
import com.hospital.entity.sys.User;
import com.hospital.mapper.sys.UserMapper;
import com.hospital.service.user.UserService;
import com.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现类
 * 包含登录核心逻辑，已优化密码校验、日志输出等逻辑
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录核心方法
     * @param userLoginDTO 登录入参（用户名、密码、验证码）
     * @return 登录结果（包含Token、用户信息）
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        log.info("用户登录请求：{}", username);

        // 1. 参数非空校验（优化：空字符串判断更严谨）
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        // 2. 查询用户（确保查询到有效用户）
        User user = userMapper.findByUserName(username.trim());
        if (user == null) {
            log.warn("用户{}不存在，登录失败", username);
            throw new BusinessException("用户不存在");
        }

        // 3. 密码比对逻辑（核心：使用BCrypt匹配，去除前后空格容错）
        String dbPassword = user.getPassword() == null ? "" : user.getPassword().trim();
        boolean passwordMatch = passwordEncoder.matches(password.trim(), dbPassword);

        // 调试日志（生产环境可注释）
        log.info("前端传入密码（去空格）：{}", password.trim());
        log.info("数据库存储密码（去空格）：{}", dbPassword);
        log.info("密码比对结果：{}", passwordMatch);

        if (!passwordMatch) {
            log.warn("用户{}密码校验未通过", username);
            throw new BusinessException("密码错误");
        }

        // 4. 状态校验（确保用户未被禁用）
        if (user.getStatus() == 0) {
            log.warn("用户{}已被禁用，登录失败", username);
            throw new BusinessException("用户已被禁用");
        }

        // 5. 生成JWT Token
        try {
            String token = jwtUtil.generateToken(user.getId(), username);
            log.info("用户[{}] Token生成成功", username);

            // 组装返回结果
            UserLoginVO userLoginVO = new UserLoginVO();
            userLoginVO.setToken(token);
            userLoginVO.setId(user.getId());
            userLoginVO.setUsername(username);
            userLoginVO.setRealName(user.getRealName());

            log.info("用户{} 登录成功", username);
            return userLoginVO;
        } catch (Exception e) {
            log.error("用户{}登录失败：生成Token异常", username, e);
            throw new BusinessException("登录异常，请稍后再试");
        }
    }

    /**
     * 【工具方法】生成BCrypt加密后的密码（用于初始化/重置用户密码）
     * 可通过Controller暴露接口，或在测试类中调用生成密文后更新数据库
     * @param rawPassword 明文密码
     * @return 加密后的密文
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new BusinessException("明文密码不能为空");
        }
        return passwordEncoder.encode(rawPassword.trim());
    }
}