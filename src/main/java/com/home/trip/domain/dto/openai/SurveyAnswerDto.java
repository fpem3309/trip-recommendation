package com.home.trip.domain.dto.openai;

import com.home.trip.domain.Survey;
import com.home.trip.domain.SurveyAnswer;
import lombok.Builder;

import java.util.List;

@Builder
public record SurveyAnswerDto(List<String> answers) {
    public static SurveyAnswerDto createSurveyPromptDto(Survey survey) {
        return SurveyAnswerDto.builder()
                .answers(survey.getSurveyAnswers().stream()
                        .map(SurveyAnswer::getAnswer)
                        .toList())
                .build();
    }
}
