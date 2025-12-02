package com.home.trip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenNotFoundException extends TokenException {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
