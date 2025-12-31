package com.home.trip.domain.dto.openai;

import com.home.trip.domain.Survey;
import com.home.trip.domain.SurveyAnswer;
import lombok.Builder;

import java.util.List;

@Builder
public record SurveyPromptDto(List<String> answers) {
    public static SurveyPromptDto createSurveyPromptDto(Survey survey) {
        return SurveyPromptDto.builder()
                .answers(survey.getSurveyAnswers().stream()
                        .map(SurveyAnswer::getAnswer)
                        .toList())
                .build();
    }
}
