package com.home.trip.filter;

import com.home.trip.service.RefreshTokenService;
import com.home.trip.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
        try {
            String accessToken = resolveToken(request);

            if (accessToken != null && jwtUtil.validateToken(accessToken)) {
                log.info("===== Valid accessToken =====\n: {}", accessToken);
                Claims claims = jwtUtil.getClaimsFromToken(accessToken);
                String username = claims.getSubject();
                String role = (String) claims.get("role");

                List<GrantedAuthority> authorities = Stream.of(role.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities); // 권한 목록도 추가

                SecurityContextHolder.getContext().setAuthentication(auth); // ⭐️SecurityContextHolder에 사용자 인증 정보 등록
            } else { // guest token 생성
                String guestToken = jwtUtil.createGuestToken();
                response.setHeader("X-Guest-Token", guestToken);
            }

        } catch (ExpiredJwtException e) { // ⭐️만료된 액세스 토큰 재발급
            String userId = e.getClaims().getSubject();

            String refreshToken = resolveRefreshToken(request); // cookie에서 가져옴
            String savedRefreshToken = refreshTokenService.getRefreshToken(userId); // redis에서 가져옴
            log.info("===== Expired accessToken =====\nuserId: {}\nrefreshToken: {}\nsavedRefreshToken: {}\nequals: {}\nvalidate: {}\n",
                    userId, refreshToken, savedRefreshToken, refreshToken.equals(savedRefreshToken), jwtUtil.validateToken(refreshToken));

            if (refreshToken.equals(savedRefreshToken) && jwtUtil.validateToken(refreshToken)) {
                String role = e.getClaims().get("role", String.class);

                List<GrantedAuthority> authorities = Stream.of(role.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                String newAccessToken = jwtUtil.generateAccessToken(userId, role);// 새 AccessToken 발급
                log.info("===== newAccessToken=====\n: {}", newAccessToken);

                response.setHeader("Authorization", "Bearer " + newAccessToken);

                Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth); // ⭐️ 새 인증 정보 등록
            } else {
                log.warn("Invalid or expired refresh token for user: {}", userId);
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        // 토큰 자체가 없으면 게스트 토큰으로 다음 필터 진행 (비회원 접근 허용)
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
