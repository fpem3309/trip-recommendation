package com.home.trip.domain;

import com.home.trip.domain.dto.openai.RecommendDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Itinerary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_recommendation_id")
    private TripRecommendation tripRecommendation;

    private int dayNumber;

    private String plan;

    public static Itinerary createItinerary(RecommendDto.ItineraryDto itineraryDto) {
        return Itinerary.builder()
                .dayNumber(itineraryDto.dayNumber())
                .plan(itineraryDto.plan())
                .build();
    }

    /**
     * setter 대신 관계 주인이 set
     * @param tripRecommendation 여행 추천
     */
    public void confirmTripRecommendation(TripRecommendation tripRecommendation) {
        this.tripRecommendation = tripRecommendation;
    }
}


