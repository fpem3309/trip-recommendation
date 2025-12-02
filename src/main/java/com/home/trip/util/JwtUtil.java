package com.home.trip.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
    final static int ONE_HOUR = 1000 * 60 * 60; // 1시간

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // 주체
                .claim("role", role) // 역할
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpTime)) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username) // 주체
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
        } catch (ExpiredJwtException e) {
            log.error("JWT expired");
            return false;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format");
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Empty JWT token");
            return false;
        } catch (Exception e) {
            log.error("Unknown JWT error", e);
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

    public String createGuestToken() {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ONE_HOUR);

        return Jwts.builder()
                .setSubject("guest")
                .claim("role", "ROLE_ANONYMOUS")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();
    }
}
