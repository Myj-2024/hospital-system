package com.hospital.config;

import com.hospital.common.utils.JwtInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 注册自定义拦截器
     * 注意：这里的 excludePathPatterns 要与 AuthController 的 RequestMapping 对应
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册 JWT 拦截器...");
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/admin/**") // 拦截所有管理端接口
                .excludePathPatterns(
                        "/admin/auth/login",    // 登录接口放行
                        "/admin/auth/refresh",  // 刷新Token接口放行
                        "/error",               // 错误页面放行
                        // 以下是 Swagger/OpenAPI 文档放行路径
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/doc.html",
                        "/favicon.ico"
                );
    }

    /**
     * 设置静态资源映射
     * 主要用于解决 Swagger/Knif4j 文档无法访问的问题
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        // 映射 knife4j/swagger 文档资源
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}