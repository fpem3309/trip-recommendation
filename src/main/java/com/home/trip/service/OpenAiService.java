package com.home.trip.service;

import com.home.trip.domain.Prompt;
import com.home.trip.domain.dto.openai.MessageDto;
import com.home.trip.domain.dto.openai.RequestDto;
import com.home.trip.domain.dto.openai.ResponseDto;
import com.home.trip.repository.PromptRepository;
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
    private final PromptRepository promptRepository;

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.base-url}")
    private String baseUrl;
    @Value("${openai.api.model}")
    private String model;

    public String getTravelRecommendation(String userPrompt) {

        Prompt prompt = promptRepository.findByRole("system").stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("정의된 프롬프트가 없습니다."));

        RequestDto chatRequest = RequestDto.builder()
                .model(model)
                .messages(List.of(
                        new MessageDto(prompt.getRole(), prompt.getContent()),
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
