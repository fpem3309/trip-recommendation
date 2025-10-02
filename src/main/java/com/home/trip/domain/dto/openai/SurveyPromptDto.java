package com.home.trip.domain.dto.openai;

import com.home.trip.domain.Survey;
import com.home.trip.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SurveyPromptDto {
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

    public static SurveyPromptDto createSurveyPromptDto(Survey survey) {
        return SurveyPromptDto.builder()
                .tripType(survey.getTripType())
                .period(survey.getPeriod())
                .peopleCount(survey.getPeopleCount())
                .budget(survey.getBudget())
                .preferenceType(survey.getPreferenceType())
                .transportation(survey.getTransportation())
                .tripStyle(survey.getTripStyle())
                .foodImportance(survey.getFoodImportance())
                .accommodation(survey.getAccommodation())
                .companion(survey.getCompanion())
                .build();
    }
}
