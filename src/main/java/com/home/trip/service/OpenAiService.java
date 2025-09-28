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

    public String getTravelRecommendation(String userPrompt) {
        // 요청 메시지 세팅
        // ❗️API 결제 후
        /*RequestDto chatRequest = RequestDto.builder()
                .model("gpt-4o-mini") // ✅ 일관된 포맷과 JSON 응답 구조화에 강함
                .messages(List.of(
                        new MessageDto("system", "너는 여행 추천을 전문으로 하는 어시스턴트야. 항상 JSON 포맷으로 대답해." +
                                " JSON 구조는 다음과 같이 고정한다:\n" +
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
        headers.setBearerAuth(apiKey);

        HttpEntity<RequestDto> request = new HttpEntity<>(chatRequest, headers);

        ResponseDto response = restTemplate.postForObject(
                baseUrl + "/chat/completions",
                request,
                ResponseDto.class
        );

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return "추천을 생성하지 못했습니다.";*/

        // ❗️임시 데이터
        return "{\n" +
                "  \"city\": \"교토\",\n" +
                "  \"country\": \"일본\",\n" +
                "  \"tripType\": \"INTERNATIONAL\",\n" +
                "  \"period\": \"5일\",\n" +
                "  \"recommendation\": \"고즈넉한 일본 전통 문화와 사찰, 벚꽃 명소가 가득해 힐링 여행으로 최고예요!\",\n" +
                "  \"itinerary\": [\n" +
                "    {\"dayNumber\": 1, \"plan\": \"교토 도착 후 기온 거리 산책 및 야사카 신사 방문\"},\n" +
                "    {\"dayNumber\": 2, \"plan\": \"아라시야마 대나무 숲, 도게츠교, 원숭이 공원 탐방\"},\n" +
                "    {\"dayNumber\": 3, \"plan\": \"금각사, 은각사, 철학의 길 투어\"},\n" +
                "    {\"dayNumber\": 4, \"plan\": \"후시미 이나리 신사에서 1만 개 도리이 체험 후 교토역 쇼핑\"},\n" +
                "    {\"dayNumber\": 5, \"plan\": \"니조성 관람 후 귀국\"}\n" +
                "  ],\n" +
                "  \"estimatedBudget\": \"약 120만원\",\n" +
                "  \"bestSeason\": \"봄(3~4월, 벚꽃 시즌) / 가을(10~11월, 단풍 시즌)\"\n" +
                "}\n";
    }
}
