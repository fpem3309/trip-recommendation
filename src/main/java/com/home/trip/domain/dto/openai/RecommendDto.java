package com.home.trip.domain.dto.openai;

import com.home.trip.domain.Itinerary;
import com.home.trip.domain.MapPlace;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

public record RecommendDto(String city, String country, String transportation, String period, String recommendation,
                           List<ItineraryDto> itinerary, String estimatedBudget, String bestSeason,
                           List<PlaceDto> googleMapPlaces) {

    @Builder
    public record ItineraryDto(int dayNumber, String plan) {
        public static ItineraryDto createRecommendItineraryDto(Itinerary itinerary) {
            return ItineraryDto.builder()
                    .dayNumber(itinerary.getDayNumber())
                    .plan(itinerary.getPlan())
                    .build();
        }
    }

    @Builder
    public record PlaceDto(int dayNumber, List<String> places) {
        public static PlaceDto createRecommendPlaceDto(MapPlace mapPlace) {
            return PlaceDto.builder()
                    .dayNumber(mapPlace.getDayNumber())
                    .places(Arrays.asList(mapPlace.getPlace().split(",")))
                    .build();
        }
    }
}
