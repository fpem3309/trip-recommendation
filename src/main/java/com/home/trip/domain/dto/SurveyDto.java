package com.home.trip.domain.dto;

import com.home.trip.domain.enums.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveyDto {
    private Long userId;
    private String guestToken;

    private TripType tripType;
    private int period;
    private int peopleCount;
    private int budget;

    private PreferenceType preferenceType;
    private Transportation transportation;
    private TripStyle tripStyle;
    private int foodImportance;

    private Accommodation accommodation;
    private Companion companion;

    private LocalDateTime createdAt;
}
