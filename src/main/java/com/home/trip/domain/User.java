package com.home.trip.domain;

import com.home.trip.domain.enums.Provider;
import com.home.trip.domain.enums.Role;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Users")
@Setter
public class User {
    @Id @GeneratedValue
    private Long id;
    private String email;
    private String nickname;

    @OneToMany(mappedBy = "user")
    private List<TripSurvey> tripSurveyList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider OAUTH_PROVIDER;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
