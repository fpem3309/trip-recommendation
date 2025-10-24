package com.home.trip.domain;

import com.home.trip.domain.dto.openai.ItineraryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
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

    public static Itinerary createItinerary(ItineraryDto itineraryDto) {
        return Itinerary.builder()
                .dayNumber(itineraryDto.getDayNumber())
                .plan(itineraryDto.getPlan())
                .build();
    }
}


