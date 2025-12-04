package com.home.trip.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key secretKey;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final long guestTokenExpTIme;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime,
            @Value("${jwt.guest_token_exp_time}") long guestTokenExpTIme
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.guestTokenExpTIme = guestTokenExpTIme;
    }

    public String generateGuestToken() {
        return Jwts.builder()
                .setSubject("guest") // 주체
                .claim("role", "ROLE_ANONYMOUS") // 역할
                .claim("type", "guest") // 토큰 타입
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + guestTokenExpTIme)) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // 주체
                .claim("role", role) // 역할
                .claim("type", "access") // 토큰 타입
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpTime)) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 주체
                .claim("type", "refresh") // 토큰 타입
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpTime)) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isAccessToken(Claims c) {
        return c.get("type").equals("access");
    }

    public boolean isGuestToken(Claims c) {
        return c.get("type").equals("guest");
    }
}
