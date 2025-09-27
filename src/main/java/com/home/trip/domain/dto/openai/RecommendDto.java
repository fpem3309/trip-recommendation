package com.home.trip.domain.dto.openai;

import lombok.Data;

import java.util.List;

@Data
public class RecommendDto {
    private String city;
    private String country;
    private String tripType;
    private String period;
    private String recommendation;
    private List<ItineraryDto> itinerary;
    private String estimatedBudget;
    private String bestSeason;
}
