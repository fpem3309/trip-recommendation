package com.home.trip.domain.dto;

import com.home.trip.domain.dto.openai.RecommendDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SurveyDataResponseDto(List<SurveyDto.SurveyAnswerDto> surveyAnswerList,
                                    List<RecommendDto.ItineraryDto> itineraryDtoList,
                                    List<RecommendDto.PlaceDto> placeDtoList,
                                    RecommendResponseDto recommendation,
                                    LocalDateTime createdAt) {

    public static SurveyDataResponseDto createRecommendNAnswerDto(List<SurveyDto.SurveyAnswerDto> surveyAnswerDtoList,
                                                                  List<RecommendDto.ItineraryDto> itineraryDtoList,
                                                                  List<RecommendDto.PlaceDto> placeDtoList,
                                                                  RecommendResponseDto recommendationDto,
                                                                  LocalDateTime createdAt) {
        return SurveyDataResponseDto.builder()
                .surveyAnswerList(surveyAnswerDtoList)
                .itineraryDtoList(itineraryDtoList)
                .placeDtoList(placeDtoList)
                .recommendation(recommendationDto)
                .createdAt(createdAt)
                .build();
    }
}