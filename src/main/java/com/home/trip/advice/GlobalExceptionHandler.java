package com.home.trip.advice;

import com.home.trip.domain.dto.ErrorResponse;
import com.home.trip.exception.InvalidRefreshTokenException;
import com.home.trip.exception.RefreshTokenNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNotFound(RefreshTokenNotFoundException ex) {
        log.warn("Handling RefreshTokenNotFoundException: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse("NO_REFRESH_TOKEN", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        log.warn("Handling InvalidRefreshTokenException: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse("INVALID_REFRESH_TOKEN", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
