package com.home.trip.domain;

import com.home.trip.domain.dto.user.UserDto;
import com.home.trip.domain.dto.user.UserStatus;
import com.home.trip.domain.dto.user.UserUpdateDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "Users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;
    private String password;
    private String email;
    private String nickname;

    @OneToMany(mappedBy = "user")
    private List<Survey> surveyList = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Provider oauth_provider;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime withdrawnAt;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public static User createUser(UserDto userDto, String encodedPassword) {
        return User.builder()
                .userId(userDto.userId())
                .password(encodedPassword)
                .email(userDto.email())
                .nickname(userDto.nickname())
                .roles(userDto.role())
                .oauth_provider(userDto.oauthProvider())
                .createdAt(userDto.createdAt())
                .updatedAt(userDto.updatedAt())
                .status(UserStatus.ACTIVE)
                .build();
    }

    public void changeUserInfo(UserUpdateDto dto) {
        if (!dto.getEmail().isBlank()) this.email = dto.getEmail();
        if (!dto.getNickname().isBlank()) this.nickname = dto.getNickname();
        if (!dto.getRole().isEmpty()) this.roles = dto.getRole();
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.status == UserStatus.WITHDRAWN;
    }
}
