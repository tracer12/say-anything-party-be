package com.test.say_anything_party.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화 (테스트용)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // 모든 요청 허용 (로그인 없이 사용 가능)
                )
                .formLogin(form -> form.disable()) // 기본 로그인 폼 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()); // HTTP 기본 인증 비활성화

        return http.build();
    }

    // 🔥 여기 추가: PasswordEncoder를 Bean으로 등록해야 함
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}