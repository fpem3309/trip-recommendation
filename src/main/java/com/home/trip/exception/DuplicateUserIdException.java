package com.home.trip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateUserIdException extends RuntimeException{
        public DuplicateUserIdException() {
            super("이미 존재하는 아이디입니다!");
        }
}
