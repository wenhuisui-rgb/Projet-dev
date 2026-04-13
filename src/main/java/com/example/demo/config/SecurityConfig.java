package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 使用 Lambda 语法禁用 CSRF
            .csrf(csrf -> csrf.disable()) 
            // 使用 Lambda 语法配置请求授权
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 暂时放行所有页面
            );
        
        return http.build();
    }
}