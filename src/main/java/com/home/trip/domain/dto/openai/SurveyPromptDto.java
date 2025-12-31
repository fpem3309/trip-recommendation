package com.home.trip.domain.dto.openai;

import com.home.trip.domain.Survey;
import com.home.trip.domain.SurveyAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SurveyPromptDto {
    private List<String> answers;

    public static SurveyPromptDto createSurveyPromptDto(Survey survey) {
        return SurveyPromptDto.builder()
                .answers(survey.getSurveyAnswers().stream()
                        .map(SurveyAnswer::getAnswer)
                        .toList())
                .build();
    }
}
