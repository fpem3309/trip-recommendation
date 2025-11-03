package com.home.trip.domain.dto;

import com.home.trip.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SurveyDto {
    private String userId;
    private String guestToken;

    private TripType tripType;
    private String period;
    private String peopleCount;
    private String budget;

    private PreferenceType preferenceType;
    private Transportation transportation;
    private TripStyle tripStyle;
    private int foodImportance;

    private Accommodation accommodation;
    private Companion companion;

    private LocalDateTime createdAt;
}
