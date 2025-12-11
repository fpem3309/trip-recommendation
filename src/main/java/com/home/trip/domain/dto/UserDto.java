package com.home.trip.domain.dto;

import com.home.trip.domain.enums.Provider;
import com.home.trip.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserDto {
    private String userId;
    private String password;
    private String email;
    private String nickname;
    private Set<Role> role;
    private Provider oauthProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
