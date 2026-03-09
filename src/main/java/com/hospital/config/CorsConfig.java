package com.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 跨域配置类（提升优先级，修复请求头/响应头配置）
 */
@Configuration
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 确保CorsFilter在所有Filter之前执行
    public CorsFilter corsFilter(){
        CorsConfiguration config = new CorsConfiguration();

        // 允许前端域名（精准配置）
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedOriginPattern("http://192.168.5.65:5173");

        // 关键修复：显式允许Authorization请求头（*不包含自定义头）
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("X-Requested-With");

        // 暴露响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Set-Cookie");

        // 允许所有HTTP方法
        config.addAllowedMethod("*");
        // 允许携带Cookie/Token
        config.setAllowCredentials(true);
        // 预检请求缓存时间（1小时）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}