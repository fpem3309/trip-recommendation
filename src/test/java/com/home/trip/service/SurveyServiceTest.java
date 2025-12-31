package com.home.trip.service;

import com.home.trip.domain.Survey;
import com.home.trip.domain.TripRecommendation;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.repository.TripRecommendationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class SurveyServiceTest {

    @Autowired
    SurveyService surveyService;

    @Autowired
    TripRecommendationRepository tripRecommendationRepository;

    @Test
    void 설문_저장() throws Exception {
        // given
        SurveyDto surveyDto = getSurveyDto();
        String userId = null;
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setHeader("X-Guest-Token", "test-guest-token");

        // when
        Long savedId = surveyService.save(surveyDto, userId, response);
        Survey findSurvey = surveyService.findBySurveyId(savedId);

        // then
        Assertions.assertThat(savedId).isEqualTo(findSurvey.getId());
    }

    @Test
    void 설문_저장시_답변_저장() throws Exception {
        // given
        SurveyDto surveyDto = getSurveyDto();
        String userId = null;
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        Long savedId = surveyService.save(surveyDto, userId, response);
        Survey findSurvey = surveyService.findBySurveyId(savedId);

        // then
        Assertions.assertThat(getAnswer(findSurvey, 0)).isEqualTo("국내여행");
        Assertions.assertThat(getAnswer(findSurvey, 1)).isEqualTo("5");
        Assertions.assertThat(getAnswer(findSurvey, 2)).isEqualTo("2");
        Assertions.assertThat(getAnswer(findSurvey, 3)).isEqualTo("1000000");
        Assertions.assertThat(getAnswer(findSurvey, 4)).isEqualTo("자연");
        Assertions.assertThat(getAnswer(findSurvey, 5)).isEqualTo("렌탈");
        Assertions.assertThat(getAnswer(findSurvey, 6)).isEqualTo("여유롭게");
        Assertions.assertThat(getAnswer(findSurvey, 7)).isEqualTo("4");
        Assertions.assertThat(getAnswer(findSurvey, 8)).isEqualTo("에어비엔비");
        Assertions.assertThat(getAnswer(findSurvey, 9)).isEqualTo("커플");
    }

    private static String getAnswer(Survey findSurvey, int index) {
        return findSurvey.getSurveyAnswers().get(index).getAnswer();
    }

    @Test
    void 설문_저장시_추천_저장() throws Exception {
        // given
        SurveyDto surveyDto = getSurveyDto();
        String userId = null;
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setHeader("X-Guest-Token", "test-guest-token");

        // when
        Long savedId = surveyService.save(surveyDto, userId, response);
        Survey findSurvey = surveyService.findBySurveyId(savedId);

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

        // then
        TripRecommendation findRecommendation = tripRecommendationRepository.findById(tripRecommendation.getId()).get();
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
                () -> surveyService.findBySurveyId(wrongId)
        );

        Assertions.assertThat(e.getMessage()).isEqualTo("해당 설문을 찾을 수 없습니다.");
    }

    private static SurveyDto getSurveyDto() {
        List<SurveyDto.SurveyAnswerDto> answerDtoList = new ArrayList<>();
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(0L,"국내여행"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(1L,"5"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(2L,"2"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(3L,"1000000"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(4L,"자연"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(5L,"렌탈"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(6L,"여유롭게"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(7L,"4"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(8L,"에어비엔비"));
        answerDtoList.add(new SurveyDto.SurveyAnswerDto(9L,"커플"));
        return new SurveyDto(null, "guest-12345", answerDtoList, LocalDateTime.now());
    }
}