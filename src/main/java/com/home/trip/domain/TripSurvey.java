package com.home.trip.domain;

import com.home.trip.domain.enums.*;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
public class TripSurvey {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "tripSurvey")
    private TripRecommendation tripRecommendation;

    private String guestToken;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    private int period;
    private int peopleCount;
    private int budget;

    @Enumerated(EnumType.STRING)
    private PreferenceType preferenceType;

    @Enumerated(EnumType.STRING)
    private Transportation transportation;

    @Enumerated(EnumType.STRING)
    private TripStyle tripStyle;
    private int foodImportance;

    @Enumerated(EnumType.STRING)
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    private Companion companion;

    private LocalDateTime createdAt;
}
