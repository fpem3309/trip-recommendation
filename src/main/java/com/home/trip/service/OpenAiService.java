package com.home.trip.service;

import com.home.trip.domain.Prompt;
import com.home.trip.domain.dto.openai.MessageDto;
import com.home.trip.domain.dto.openai.RequestDto;
import com.home.trip.domain.dto.openai.ResponseDto;
import com.home.trip.domain.dto.openai.SurveyPromptDto;
import com.home.trip.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenAiService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final PromptRepository promptRepository;

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.base-url}")
    private String baseUrl;
    @Value("${openai.api.model}")
    private String model;

    /**
     * 프롬프트 찾기
     *
     * @param id 찾을 프롬프트의 Id(_id)
     * @return 해당 Id(_id)를 가진 Prompt
     */
    public Optional<Prompt> findPromptById(String id) {
        return promptRepository.findById(id);
    }

    /**
     * 활성화된 프롬프트 찾기
     *
     * @param role 찾을 프롬프트의 role
     * @return 해당 role을 가진 Prompt
     */
    public Optional<Prompt> findActivePromptByRole(String role) {
        return promptRepository.findActiveByRole(role);
    }

    /**
     * OpenAI에 회원 프롬프트로 추천 결과 받기
     *
     * @param userPrompt 설문 질의응답
     * @return 설문 추천 결과
     * @throws IllegalArgumentException 해당 role가 없거나 Active인 Prompt가 없을 때
     */
    public String getTravelRecommendation(String userPrompt) {

        Prompt systemPrompt = findActivePromptByRole("system")
                .orElseThrow(() -> new IllegalArgumentException("정의된 프롬프트가 없습니다."));

        log.info("OpenAI prompt: {}: {}", systemPrompt.getRole(), systemPrompt.getContent());

        RequestDto chatRequest = RequestDto.builder()
                .model(model)
                .messages(List.of(
                        new MessageDto(systemPrompt.getRole(), systemPrompt.getContent()),
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

        log.info("OpenAI API response: {}", response);

        if (response != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content();
        }
        return "추천을 생성하지 못했습니다.";
    }

    /**
     * 모든 프롬프트 찾기
     *
     * @return 모든 프롬프트 리스트
     */
    public List<SurveyPromptDto> findAllPrompts() {
        List<Prompt> systemPrompt = promptRepository.findAll();
        return systemPrompt.stream()
                .map(SurveyPromptDto::createSurveyPromptDto)
                .toList();
    }

    /**
     * 새 프롬프트 저장
     *
     * @param surveyPromptDto 저장할 프롬프트 정보
     */
    @Transactional
    public String savePrompt(SurveyPromptDto surveyPromptDto) {
        Prompt prompt = Prompt.createPrompt(surveyPromptDto);
        deactivateActivePrompt(surveyPromptDto);
        promptRepository.save(prompt);
        return prompt.getId();
    }

    /**
     * 프롬프트 업데이트
     *
     * @param surveyPromptDto 업데이트할 프롬프트 정보
     * @throws IllegalArgumentException 해당 Id를 가진 Prompt가 없을 때
     */
    @Transactional
    public String updatePrompt(String promptId, SurveyPromptDto surveyPromptDto) {
        Prompt findPrompt = findPromptById(promptId)
                .orElseThrow(() -> new IllegalArgumentException("정의된 프롬프트가 없습니다."));
        deactivateActivePrompt(surveyPromptDto);
        findPrompt.setSurveyPrompt(surveyPromptDto); // 정보 업데이트
        promptRepository.save(findPrompt);
        return findPrompt.getId();
    }

    /**
     * 이전 Active Prompt 비활성화
     *
     * @param surveyPromptDto 업데이트할 프롬프트
     */
    private void deactivateActivePrompt(SurveyPromptDto surveyPromptDto) {
        if (surveyPromptDto.isActive() == 1) {
            findActivePromptByRole(surveyPromptDto.role())
                    .ifPresent(activePrompt -> {
                        activePrompt.deactivatePrompt();
                        promptRepository.save(activePrompt);
                    });
        }
    }

    /**
     * 프롬프트 삭제
     *
     * @param promptId 삭제할 프롬프트 Id(_id)
     * @throws IllegalArgumentException 해당 Id를 가진 Prompt가 없을 때
     */
    @Transactional
    public String deletePrompt(String promptId) {
        Prompt findPrompt = findPromptById(promptId)
                .orElseThrow(() -> new IllegalArgumentException("정의된 프롬프트가 없습니다."));
        promptRepository.delete(findPrompt);
        return findPrompt.getId();
    }
}
