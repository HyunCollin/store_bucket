package com.store.store_bucket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 리소스 모두 허용
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**"
                        ).permitAll()
                        // 그 외 모든 요청은 로그인 필요 (또는 .permitAll()로 전체 개방 가능)
//                        .anyRequest().authenticated()
                        // TODO 개발 완료 후 권한 회수
                        .anyRequest().permitAll()
                )
                // 기본 로그인 폼 유지 (원치 않으면 disable() 처리)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}