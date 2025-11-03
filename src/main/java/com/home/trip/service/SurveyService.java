package com.home.trip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.trip.domain.Itinerary;
import com.home.trip.domain.Survey;
import com.home.trip.domain.TripRecommendation;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.domain.dto.openai.RecommendDto;
import com.home.trip.domain.dto.openai.SurveyPromptDto;
import com.home.trip.repository.SurveyRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final OpenAiService openAiService;

    public Long save(SurveyDto surveyDto, HttpServletResponse response) {
        if (surveyDto.getUserId() == null) { // TODO: Security에서 유저 정보 가져오는 방식으로 변경 필요
            String token = response.getHeader("X-Guest-Token");
            surveyDto.setGuestToken(token);
        }
        Survey survey = Survey.createSurvey(surveyDto);
        TripRecommendation tripRecommendation = TripRecommendation.createTripRecommendation(survey); // 상세 내용 제외 저장
        survey.setTripRecommendation(tripRecommendation);
        surveyRepository.save(survey);
        return survey.getId();
    }

    public RecommendDto getRecommend(Long surveyId) {
        ObjectMapper objectMapper = new ObjectMapper();
        Survey findSurvey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설문을 찾을 수 없습니다."));

        try {
            String json = objectMapper.writeValueAsString(SurveyPromptDto.createSurveyPromptDto(findSurvey));

            String prompt = "다음은 사용자가 작성한 여행 설문 응답이야. \n" +
                    "설문 데이터:\n" + json;

            String travelRecommendation = openAiService.getTravelRecommendation(prompt)
                    .replaceAll("```", "")
                    .replaceAll("\\)\\]\\)\\]\\)$", "")
                    .trim();

            return objectMapper.readValue(travelRecommendation, RecommendDto.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRecommendation(Long surveyId, RecommendDto recommendDto) {
        TripRecommendation tripRecommendation = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."))
                .getTripRecommendation();

        recommendDto.getItinerary()
                .forEach(dto -> tripRecommendation.addItinerary(Itinerary.createItinerary(dto)));

        tripRecommendation.setRecommendationTrip(recommendDto);
    }

    public RecommendDto recommendation(Long surveyId) {
        try {
            RecommendDto recommend = getRecommend(surveyId); // AI 추천 내용
            updateRecommendation(surveyId, recommend); // 설문 DB 업데이트
            return recommend;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
