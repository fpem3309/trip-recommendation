package com.home.trip.domain.dto.openai;

import java.util.List;

public record RecommendDto(String city, String country, String transportation, String period, String recommendation,
                           List<ItineraryDto> itinerary, String estimatedBudget, String bestSeason, List<PlaceDto> googleMapPlaces) {
    public record ItineraryDto(int dayNumber, String plan) {}
    public record PlaceDto(int dayNumber, List<String> places) {}
}
