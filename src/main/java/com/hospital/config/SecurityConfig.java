package com.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 禁用 HTTP Basic 认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 3. 禁用表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 4. 禁用注销功能
                .logout(AbstractHttpConfigurer::disable)
                // 5. 配置会话策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 6. 启用CORS（使用自定义配置）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 7. 授权规则（核心：放行OPTIONS请求）
                .authorizeHttpRequests(auth -> auth
                        // 放行登录/刷新Token接口
                        .requestMatchers("/admin/auth/login", "/admin/auth/refresh").permitAll()
                        // 放行Swagger文档
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/doc.html").permitAll()
                        // 关键修复：放行所有OPTIONS预检请求
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // 其他请求允许（JWT拦截器做细粒度校验）
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * 密码编码器 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 适配Security的CORS配置（与CorsConfig保持一致）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedOriginPattern("http://192.168.5.65:5173");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}