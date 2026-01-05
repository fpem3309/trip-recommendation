package com.home.trip.domain.dto;

import com.home.trip.domain.TripRecommendation;
import lombok.Builder;

@Builder
public record RecommendResponseDto(Long id, String bestSeason, String city, String country,
                                   String transportation, String estimatedBudget, String period,
                                   String recommendation) {

    public static RecommendResponseDto createRecommendDto(TripRecommendation recommendation) {
        return RecommendResponseDto.builder()
                .id(recommendation.getId())
                .bestSeason(recommendation.getBestSeason())
                .city(recommendation.getCity())
                .country(recommendation.getCountry())
                .transportation(recommendation.getTransportation())
                .estimatedBudget(recommendation.getEstimatedBudget())
                .period(recommendation.getPeriod())
                .recommendation(recommendation.getRecommendation())
                .build();
    }
}
