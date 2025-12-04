package com.home.trip.filter;

import com.home.trip.service.RefreshTokenService;
import com.home.trip.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // ⭐️OncePerRequestFilter: Http Request의 요청에 대해 한번만 실행

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        log.info("===== accessToken =====\n: {}", accessToken);

        try {
            if (accessToken != null) {
                // 1. accessToken 검증, 만료시 ExpiredJwtException 예외 발생
                jwtUtil.validateToken(accessToken);
                Claims claims = jwtUtil.getClaimsFromToken(accessToken);

                // 2. 토큰 타입 체크
                if (jwtUtil.isGuestToken(claims)) { // guest: 인증 생성하지 않고 다음 필터로
                    log.info("Guest token detected - skip authentication");
                    filterChain.doFilter(request, response);
                    return;
                }

                if (!jwtUtil.isAccessToken(claims)) { // access 타입이 아닌 경우: (refresh 토큰) 그냥 통과
                    log.warn("Token is not an access token - skip auth");
                    filterChain.doFilter(request, response);
                    return;
                }

                // 3. 정상적인 Access Token: Authentication 세팅(userId, role)
                setAuthentication(claims.getSubject(), (String) claims.get("role"));
            } else { // accessToken 없으면 guest token 생성
                String guestToken = jwtUtil.generateGuestToken();
                response.setHeader("X-Guest-Token", guestToken);
            }

        } catch (ExpiredJwtException e) { // ⭐️액세스 토큰 만료 예외
            log.info("ExpiredJwtException");
            sendUnauthorized(response, "ACCESS_TOKEN_EXPIRED");
            return;
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            sendUnauthorized(response, "INVALID_ACCESS_TOKEN");
            return;
        }

        // 정상적인 경우: 다음 필터 진행
        filterChain.doFilter(request, response);
    }

    /**
     * 예외 발생시 메시지와 함께 JSON으로 응답(401)
     * @param response
     * @param message 예외 메시지(만료 또는 유효하지 않는 토큰)
     * @throws IOException
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    /**
     * header에서 Access Token 가져오기
     * @param request
     * @return 토큰 있으면 토큰, 아니면 null
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 권한 GrantedAuthority List로 리턴
     * @param userId 사용자 ID
     * @param role 사용자 권한
     */
    private static void setAuthentication(String userId, String role) {
        List<SimpleGrantedAuthority> authorities = Stream.of(role.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, authorities); // ID, 권한 목록 추가
        SecurityContextHolder.getContext().setAuthentication(auth); // ⭐️SecurityContextHolder에 사용자 인증 정보 등록
    }
}
