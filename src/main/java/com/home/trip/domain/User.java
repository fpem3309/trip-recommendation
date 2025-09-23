package com.home.trip.domain;

import com.home.trip.domain.dto.UserDto;
import com.home.trip.domain.enums.Provider;
import com.home.trip.domain.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    private String password;
    private String email;
    private String nickname;

    @OneToMany(mappedBy = "user")
    private List<Survey> surveyList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider oauth_provider;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static User createUser(UserDto userDto, String encodedPassword) {
        return User.builder()
                .userId(userDto.getUserId())
                .password(encodedPassword)
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .role(userDto.getRole())
                .oauth_provider(userDto.getOauthProvider())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .build();
    }
}
