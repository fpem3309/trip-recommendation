package com.home.trip.domain.dto.user;

import com.home.trip.domain.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserUpdateDto {
    private String userId;
    private String email;
    private String nickname;
    private Set<Role> role;
}
