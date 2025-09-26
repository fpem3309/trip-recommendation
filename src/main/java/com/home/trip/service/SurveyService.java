package com.home.trip.service;

import com.home.trip.domain.Survey;
import com.home.trip.domain.TripRecommendation;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.repository.SurveyRepository;
import com.home.trip.repository.TripRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final TripRecommendationRepository tripRecommendationRepository;

    public void save(SurveyDto surveyDto) {
        Survey survey = Survey.createSurvey(surveyDto);
        TripRecommendation tripRecommendation = TripRecommendation.createTripRecommendation(survey);
        surveyRepository.save(survey);
        tripRecommendationRepository.save(tripRecommendation);
    }
}
