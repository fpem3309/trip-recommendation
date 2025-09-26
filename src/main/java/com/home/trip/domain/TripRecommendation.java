package com.home.trip.domain;

import com.home.trip.domain.enums.Badge;
import com.home.trip.domain.enums.RecommendationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRecommendation {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_survey_id")
    private Survey survey;

    @Enumerated(EnumType.STRING)
    private Badge badge;

    @Lob
    private String recommendationContent;

    @Enumerated(EnumType.STRING)
    private RecommendationStatus status;

    public static TripRecommendation createTripRecommendation(Survey survey) {
        return TripRecommendation.builder()
                .survey(survey)
                .badge(null)
                .recommendationContent(null)
                .status(RecommendationStatus.PENDING)
                .build();
    }

}
