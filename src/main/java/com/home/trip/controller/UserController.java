package com.home.trip.controller;

import com.home.trip.domain.dto.UserDto;
import com.home.trip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user")
    public void joinUser(@RequestBody UserDto userDto) {
        userService.join(userDto);
    }
}
