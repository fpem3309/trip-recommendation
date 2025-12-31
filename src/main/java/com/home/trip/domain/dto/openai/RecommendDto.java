package com.home.trip.domain.dto.openai;

import java.util.List;

public record RecommendDto(String city, String country, String tripType, String period, String recommendation,
                           List<ItineraryDto> itinerary, String estimatedBudget, String bestSeason) {
}
