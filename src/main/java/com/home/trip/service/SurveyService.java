package com.home.trip.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.trip.domain.*;
import com.home.trip.domain.dto.RecommendResponseDto;
import com.home.trip.domain.dto.SurveyDataResponseDto;
import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.domain.dto.openai.RecommendDto;
import com.home.trip.domain.dto.openai.SurveyPromptDto;
import com.home.trip.domain.enums.RecommendationStatus;
import com.home.trip.repository.SurveyRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final OpenAiService openAiService;
    private final UserService userService;

    /**
     * Survey & TripRecommendation 저장(추천 내용은 빈 상태, 상태는 PENDING)
     *
     * @param surveyDto 설문 답변
     * @param userId    회원 Id
     * @param response
     * @return 저장한 설문 Id
     */
    @Transactional
    public Long save(SurveyDto surveyDto, String userId, HttpServletResponse response) {

        // 1. User or Guest 설정
        if (userId != null) { // 로그인 회원
            User findUser = userService.findByUserId(userId);
            surveyDto.setUser(findUser);
        } else { // 게스트
            String guestToken = response.getHeader("X-Guest-Token");
            surveyDto.setGuestToken(guestToken);
        }

        // 2. Survey Entity 생성
        Survey survey = Survey.createSurvey(surveyDto);

        // 3. Survey <-> SurveyAnswer 설정
        surveyDto.getSurveyAnswers().forEach(answer -> {
            SurveyAnswer surveyAnswer = SurveyAnswer.createSurveyAnswer(answer);
            survey.addAnswer(surveyAnswer);
        });

        // 4. TripRecommendation 생성 & Survey <-> TripRecommendation 설정
        TripRecommendation tripRecommendation = TripRecommendation.createTripRecommendation(survey);
        survey.setTripRecommendation(tripRecommendation);

        surveyRepository.save(survey);
        return survey.getId();
    }

    /**
     * 설문 찾기
     *
     * @param surveyId 찾을 설문 Id
     * @return 해당 Id를 가진 Survey
     * @throws IllegalStateException 해당 Id를 가진 Survey가 없을 때
     */
    public Survey findBySurveyId(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설문을 찾을 수 없습니다."));
    }

    /**
     * 설문 답변으로 여행 추천
     *
     * @param surveyId 추천할 설문의 Id
     * @return 추천 결과 DTO
     */
    @Transactional
    public RecommendDto recommendation(Long surveyId) {
        try {
            Survey findSurvey = findBySurveyId(surveyId);
            RecommendDto recommend = getRecommend(findSurvey);
            updateRecommendation(findSurvey.getTripRecommendation(), recommend);
            return recommend;
        } catch (Exception e) {
            log.error("추천 중 예외 발생 ", e);
            throw new IllegalStateException("추천 중 예외 발생");
        }
    }

    /**
     * OpenAI API로 여행 추천
     *
     * @param survey 설문 내용
     * @return 여행 추천 내용
     */
    private RecommendDto getRecommend(Survey survey) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String answer = objectMapper.writeValueAsString(SurveyPromptDto.createSurveyPromptDto(survey));

            log.info("answer: {}", answer);

            String prompt = "다음은 사용자가 작성한 여행 설문 응답이야. \n" +
                    "설문 데이터:\n" + answer;

            String travelRecommendation = openAiService.getTravelRecommendation(prompt)
                    .replaceAll("```", "")
                    .replaceAll("json", "")
                    .replaceAll("\\)\\]\\)\\]\\)$", "")
                    .trim();

            return objectMapper.readValue(travelRecommendation, RecommendDto.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AI 추천 내용 적용
     *
     * @param tripRecommendation AI 추천 Entity
     * @param recommendDto       AI 추천 내용
     */
    private void updateRecommendation(TripRecommendation tripRecommendation, RecommendDto recommendDto) {

        // 추천 일자별 계획
        recommendDto.itinerary()
                .forEach(dto -> tripRecommendation.addItinerary(Itinerary.createItinerary(dto)));

        // 추천 일자별 장소
        recommendDto.googleMapPlaces()
                .forEach(dto -> tripRecommendation.addMapPlaces(MapPlace.createMapPlace(dto)));

        tripRecommendation.setRecommendationTrip(recommendDto);
    }

    /**
     * 회원의 설문 추천 결과 리스트
     *
     * @param userId 회원 아이디
     * @return 추천 결과
     */
    public List<RecommendResponseDto> getUserSurveyReulstList(String userId) {
        User findUser = userService.findByUserId(userId);
        return findUser.getSurveyList().stream()
                .filter(survey -> survey.getTripRecommendation().getStatus().equals(RecommendationStatus.COMPLETED))
                .map(survey -> RecommendResponseDto.createRecommendDto(survey.getTripRecommendation()))
                .toList();
    }

    /**
     * 회원의 설문의 추천 결과, 질문&답변, 일자별 계획, 일자별 장소
     *
     * @param surveyId 설문 아이디
     * @return 질문&답변 및 모든 추천 결과
     */
    public SurveyDataResponseDto getSurveyResult(Long surveyId) {
        Survey findSurvey = findBySurveyId(surveyId);

        // 추천 결과
        TripRecommendation tripRecommendation = findSurvey.getTripRecommendation();

        if (!tripRecommendation.getStatus().equals(RecommendationStatus.COMPLETED)) {
            throw new IllegalArgumentException("완료되지 않은 설문입니다.");
        }

        // 질문:답 형태 리스트
        List<SurveyDto.SurveyAnswerDto> surveyAnswerDtoList = findSurvey.getSurveyAnswers().stream()
                .map(SurveyDto.SurveyAnswerDto::createSurveyAnswerDto)
                .toList();

        // 일자별 계획 리스트
        List<RecommendDto.ItineraryDto> itineraryDtoList = tripRecommendation.getItinerary().stream()
                .map(RecommendDto.ItineraryDto::createRecommendItineraryDto)
                .toList();

        // 일자별 장소 구글맵 리스트
        List<RecommendDto.PlaceDto> placeDtoList = tripRecommendation.getMapPlaceList().stream()
                .map(RecommendDto.PlaceDto::createRecommendPlaceDto)
                .toList();

        RecommendResponseDto recommendDto = RecommendResponseDto.createRecommendDto(tripRecommendation);
        return SurveyDataResponseDto.createRecommendNAnswerDto(surveyAnswerDtoList, itineraryDtoList, placeDtoList, recommendDto, findSurvey.getCreatedAt());
    }
}
