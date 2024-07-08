package org.spring.aicloud.config;

import cn.hutool.db.Session;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 框架配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private LoginAuthenticationFilter loginAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用明文验证
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF 验证
                .formLogin(AbstractHttpConfigurer::disable) // 禁用默认登录页
                .logout(AbstractHttpConfigurer::disable) // 禁用默认注销
                .headers(AbstractHttpConfigurer::disable) // 禁用默认头信息
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用会话管理
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // 允许访问
                                "/layui/**",
                                "/js/**",
                                "/css/**",
                                "/login.html",
                                "/index.html",
                                "/register.html",
                                "/user/login",
                                "/user/register",
                                "/captcha/create",
                                "/openai/chat",
                                "/openai/draw",
                                "/xunfei/chat",
                                "/xunfei/draw"
                        ).permitAll()
                        .anyRequest().authenticated() // 其他请求需要认证
                )
                // 添加自定义过滤器
                .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
