package com.home.trip.controller;

import com.home.trip.domain.dto.UserDto;
import com.home.trip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "회원 관련 API")
@RestController
@RequestMapping("/api-user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user")
    @Operation(summary = "회원가입", description = "새로운 회원을 등록")
    public void joinUser(@RequestBody UserDto userDto) {
        userService.join(userDto);
    }
}
