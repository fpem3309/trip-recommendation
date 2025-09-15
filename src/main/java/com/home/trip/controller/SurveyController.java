package com.home.trip.controller;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-survey")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @PostMapping("/survey")
    public void saveSurvey(@RequestBody SurveyDto surveyDto) {
        surveyService.save(surveyDto);
    }
}
