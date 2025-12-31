package com.home.trip.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.trip.domain.dto.user.UserLoginDto;
import com.home.trip.service.RefreshTokenService;
import com.home.trip.util.JwtUtil;
import com.home.trip.util.UserUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;
    private final UserUtil userUtil;
    private final RefreshTokenService refreshTokenService;

    public JsonUsernamePasswordAuthFilter(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, JwtUtil jwtUtil, UserUtil userUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userUtil = userUtil;
        this.refreshTokenService = refreshTokenService;
        setFilterProcessesUrl("/api/auth/login"); // 로그인 엔드포인트
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserLoginDto userLoginDto = objectMapper.readValue(request.getInputStream(), UserLoginDto.class);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userLoginDto.userId(),
                            userLoginDto.password()
                    );

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ 로그인 성공 시 실행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String role = userUtil.userRoleToStringWithComma(authResult.getAuthorities(), GrantedAuthority::getAuthority);
        String username = authResult.getName();
        String accessToken = jwtUtil.generateAccessToken(username, role);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        // redis에 저장
        refreshTokenService.saveRefreshToken(username, refreshToken);

        response.addHeader("Set-Cookie",
                "refresh=" + refreshToken + "; HttpOnly; Path=/; Max-Age=604800; SameSite=None; Secure");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\":\"" + accessToken + "\"}");
    }

    // ❎ 로그인 실패 시 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        log.info("failed message: {}", failed.getMessage());
        response.getWriter().write("{\"message\":\"" + failed.getMessage() + "\"}");
    }
}
