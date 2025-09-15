package com.home.trip.service;

import com.home.trip.domain.Survey;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    public void save(SurveyDto surveyDto) {
        Survey survey = Survey.createSurvey(surveyDto);
        surveyRepository.save(survey);
    }
}
