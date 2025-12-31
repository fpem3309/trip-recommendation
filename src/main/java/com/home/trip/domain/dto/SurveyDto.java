package com.home.trip.domain.dto;

import com.home.trip.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SurveyDto {
    private User user;
    private String guestToken;
    private List<SurveyAnswerDto> surveyAnswers;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    public static class SurveyAnswerDto{
        private Long questionId;
        private String answer;
    }
}
