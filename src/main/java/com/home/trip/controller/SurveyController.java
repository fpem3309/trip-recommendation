package com.home.trip.controller;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.domain.dto.openai.RecommendDto;
import com.home.trip.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Tag(name = "설문", description = "설문 관련 API")
@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @Operation(summary = "설문 등록", description = "작성한 설문을 등록하고 AI 여행지 추천받기")
    @PostMapping
    public ResponseEntity<RecommendDto> saveSurvey(@RequestBody SurveyDto surveyDto, HttpServletResponse response) {

        String userId = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .orElse(null);

        Long surveyId = surveyService.save(surveyDto, userId, response);
        RecommendDto recommendDto = surveyService.recommendation(surveyId);
        return ResponseEntity.ok(recommendDto);
    }
}
