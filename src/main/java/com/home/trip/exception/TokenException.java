package com.home.trip.exception;

// 토큰 관련 예외
public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
