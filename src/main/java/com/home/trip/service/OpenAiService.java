package com.home.trip.service;

import com.home.trip.domain.dto.openai.MessageDto;
import com.home.trip.domain.dto.openai.RequestDto;
import com.home.trip.domain.dto.openai.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.base-url}")
    private String baseUrl;
    @Value("${openai.api.model}")
    private String model;

    public String getTravelRecommendation(String userPrompt) {
        // 요청 메시지 세팅
        // ❗️API 결제 후
        RequestDto chatRequest = RequestDto.builder()
                .model(model)
                .messages(List.of(
                        new MessageDto("system", "너는 여행 추천을 전문으로 하는 어시스턴트야. 항상 JSON 포맷으로 대답해." +
                                " JSON 구조는 다음과 같이 고정한다, 다른 기호는 사용하지 마. :\n" +
                                "{\n" +
                                "  \"city\": \"도시명\",\n" +
                                "  \"country\": \"국가명\",\n" +
                                "  \"tripType\": \"여행유형(DOMESTIC/INTERNATIONAL)\",\n" +
                                "  \"period\": \"여행기간(일수)\",\n" +
                                "  \"recommendation\": \"추천 이유\",\n" +
                                "  \"itinerary\": [\n" +
                                "    {\"dayNumber\": 1, \"plan\": \"첫째 날 일정\"},\n" +
                                "    {\"dayNumber\": 2, \"plan\": \"둘째 날 일정\"}\n" +
                                "  ],\n" +
                                "  \"estimatedBudget\": \"예상 경비(원)\",\n" +
                                "  \"bestSeason\": \"여행 추천 계절\"\n" +
                                "}"),
                        new MessageDto("user", userPrompt)
                ))
                .temperature(0.7)
                .build();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(apiKey);

        HttpEntity<RequestDto> request = new HttpEntity<>(chatRequest, headers);

        ResponseDto response = restTemplate.postForObject(
                baseUrl + "/chat/completions",
                request,
                ResponseDto.class
        );

        log.info("openAI API response: {}", response);

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return "추천을 생성하지 못했습니다.";
    }
}
