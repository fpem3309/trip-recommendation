package com.home.trip;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**", // ui
                                "/v3/api-docs/**", // api 문서
                                "/api-**/**"
                        ).permitAll()
                        .anyRequest().authenticated()

                )
                .csrf(csrf -> csrf.disable()); // REST API 서버니까 CSRF 끄기
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
