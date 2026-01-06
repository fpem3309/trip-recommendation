package com.home.trip.domain;

import com.home.trip.domain.dto.openai.RecommendDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MapPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_recommendation_id")
    private TripRecommendation tripRecommendation;

    private int dayNumber;

    private String place;

    public static MapPlace createMapPlace(RecommendDto.PlaceDto placeDto) {
        return MapPlace.builder()
                .dayNumber(placeDto.dayNumber())
                .place(String.join(",", placeDto.places()))
                .build();
    }

    /**
     * setter 대신 관계 주인이 set
     *
     * @param tripRecommendation 여행 추천
     */
    public void confirmTripRecommendation(TripRecommendation tripRecommendation) {
        this.tripRecommendation = tripRecommendation;
    }
}
