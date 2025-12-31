package com.home.trip.domain.dto.user;

import com.home.trip.domain.enums.Provider;
import com.home.trip.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDto(String userId, String password, String email, String nickname,
                      Set<Role> role, Provider oauthProvider, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
