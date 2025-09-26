package com.home.trip.controller;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "설문", description = "설문 관련 API")
@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @Operation(summary = "설문 등록", description = "작성한 설문을 등록")
    @PostMapping
    public void saveSurvey(@RequestBody SurveyDto surveyDto) {
        surveyService.save(surveyDto);
    }
}
