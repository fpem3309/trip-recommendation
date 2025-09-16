package com.home.trip.domain.dto;

import com.home.trip.domain.enums.Provider;
import com.home.trip.domain.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private Role role;
    private Provider oauthProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
