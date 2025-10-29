package com.home.trip.service;

import com.home.trip.domain.Survey;
import com.home.trip.domain.TripRecommendation;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.domain.enums.*;
import com.home.trip.repository.SurveyRepository;
import com.home.trip.repository.TripRecommendationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class SurveyServiceTest {

    @Autowired
    SurveyService surveyService;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    TripRecommendationRepository tripRecommendationRepository;

    @Test
    void 설문_저장() throws Exception {
        // given
        SurveyDto surveyDto = getSurveyDto();

        // when
        Long savedId = surveyService.save(surveyDto);
        Survey findSurvey = surveyRepository.findById(savedId).get();

        // then
        Assertions.assertThat(savedId).isEqualTo(findSurvey.getId());
    }

    @Test
    void 설문_저장시_추천_저장() throws Exception {
        // given
        SurveyDto surveyDto = getSurveyDto();

        // when
        Long savedId = surveyService.save(surveyDto);
        Survey findSurvey = surveyRepository.findById(savedId).get();

        // then
        Assertions.assertThat(findSurvey.getTripRecommendation()).isNotNull();
    }

    @Test
    void 추천_저장() throws Exception {
        // given
        SurveyDto surveyDto = getSurveyDto();

        Survey survey = Survey.createSurvey(surveyDto);
        TripRecommendation tripRecommendation = TripRecommendation.createTripRecommendation(survey);

        // when
        tripRecommendationRepository.save(tripRecommendation);
        TripRecommendation findRecommendation = tripRecommendationRepository.findById(tripRecommendation.getId()).get();

        // then
        Assertions.assertThat(tripRecommendation.getId()).isEqualTo(findRecommendation.getId());
    }

    @Test
    void 존재하지_않는_설문() throws Exception {
        // given
        Long wrongId = 999L;

        // when
        // then
        IllegalArgumentException e = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> surveyService.getRecommend(wrongId)
        );

        Assertions.assertThat(e.getMessage()).isEqualTo("해당 설문을 찾을 수 없습니다.");
    }

    private static SurveyDto getSurveyDto() {
        return new SurveyDto(null,
                "guest-12345",
                TripType.DOMESTIC,
                "5",
                "2",
                "1000000",
                PreferenceType.NATURE,
                Transportation.RENTAL,
                TripStyle.LEISURELY,
                4,
                Accommodation.ANY,
                Companion.COUPLE,
                LocalDateTime.now());
    }
}