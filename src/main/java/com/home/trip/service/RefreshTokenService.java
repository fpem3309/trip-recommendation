package com.home.trip.service;

import com.home.trip.exception.InvalidRefreshTokenException;
import com.home.trip.exception.RefreshTokenNotFoundException;
import com.home.trip.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redis;
    private final JwtUtil jwtUtil;
    private static final long TIME_OUT = 7;

    public void saveRefreshToken(String userId, String refreshToken) {
        log.info("Save redis key: {}, refreshToken: {}", userId, refreshToken);
        redis.opsForValue().set("RT:" + userId, refreshToken, TIME_OUT, TimeUnit.DAYS);
    }

    public String getRefreshToken(String userId) {
        return redis.opsForValue().get("RT:" + userId);
    }

    public void deleteRefreshToken(String userId) {
        log.info("Delete redis key: {}", userId);
        redis.delete("RT:" + userId);
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 발급합니다.
     * @param refreshToken Cookie에서 추출한 Refresh Token
     * @return 새로 발급된 Access Token
     * @throws RefreshTokenNotFoundException Refresh Token이 요청에 없을 경우
     * @throws InvalidRefreshTokenException Refresh Token이 유효하지 않거나, DB(Redis)에 저장된 값과 다를 경우
     */
    public String refreshNewAccessToken(String refreshToken) {
        // 1. 토큰 존재 여부 확인
        if (!StringUtils.hasText(refreshToken)) {
            throw new RefreshTokenNotFoundException("Refresh Token이 요청에 존재하지 않습니다.");
        }

        // 2. 토큰 유효성 검증 (만료, 서명 등)
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh Token이 유효하지 않습니다. (만료 또는 서명 오류)");
        }

        // 3. 토큰으로 사용자 ID(username)와 권한 가져오기
        Claims claims = jwtUtil.getClaimsFromToken(refreshToken);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        // 4. Redis에 저장된 토큰과 일치 여부 확인
        if (!isRefreshTokenValid(username, refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh Token이 탈취되었거나 만료되었습니다.");
        }

        // 5. 모든 검증 통과 시, 새로운 Access Token 발급
        return jwtUtil.generateAccessToken(username, role);
    }

    /**
     * Redis에 저장된 Refresh Token과 일치하는지 확인합니다.
     * @param username 사용자 ID
     * @param refreshToken 검증할 Refresh Token
     * @return 유효하면 true, 아니면 false
     */
    private boolean isRefreshTokenValid(String username, String refreshToken) {
        String findRefreshToken = getRefreshToken(username);
        return findRefreshToken != null && findRefreshToken.equals(refreshToken);
    }
}

