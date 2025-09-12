package com.home.trip.domain;

import com.home.trip.domain.enums.Badge;
import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Setter
public class TripRecommendation {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_survey_id")
    private TripSurvey tripSurvey;

    @Enumerated(EnumType.STRING)
    private Badge badge;
    private String title;
    private String description;
}
