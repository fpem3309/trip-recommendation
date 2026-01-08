package com.home.trip.domain.dto.openai;

import com.home.trip.domain.Prompt;
import lombok.Builder;

@Builder
public record SurveyPromptDto(String id, String role, String content, Integer isActive) {
    public static SurveyPromptDto createSurveyPromptDto(Prompt prompt) {
        return SurveyPromptDto.builder()
                .id(prompt.getId())
                .role(prompt.getRole())
                .content(prompt.getContent())
                .isActive(prompt.getIsActive())
                .build();
    }
}
