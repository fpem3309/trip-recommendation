package com.home.trip;

import com.home.trip.filter.JsonUsernamePasswordAuthFilter;
import com.home.trip.filter.JwtAuthenticationFilter;
import com.home.trip.service.CustomUserDetailService;
import com.home.trip.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailsService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // REST API 서버니까 CSRF 끄기
                .formLogin(form -> form.disable()) // 폼 로그인 끄기
                .httpBasic(basic -> basic.disable()) // Basic 인증 끄기
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**", // ui
                                "/v3/api-docs/**", // api 문서
                                "/api/survey", // 설문
                                "/api/survey/**", // 설문
                                "/api/auth/**" // 회원 관련
                        ).permitAll()
                        .anyRequest().authenticated()

                )
                // ⭐ 로그인용 커스텀 필터 등록 (UsernamePasswordAuthenticationFilter 대체)
                .addFilterBefore(new JsonUsernamePasswordAuthFilter(authenticationManager, jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)

                // ⭐ JWT 검증 필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // AuthenticationManager Bean으로 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
