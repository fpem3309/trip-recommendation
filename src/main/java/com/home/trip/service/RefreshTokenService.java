package com.home.trip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redis;
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
}

