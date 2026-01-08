package com.home.trip.domain;

import com.home.trip.domain.dto.openai.SurveyPromptDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prompts")
public class Prompt {
    @Id
    private String id;
    private String role;
    private String content;
    private Integer isActive;

    /**
     * 변경할 정보로 set
     * @param surveyPromptDto role, 프롬프트 텍스트, 활성화
     */
    public void setSurveyPrompt(SurveyPromptDto surveyPromptDto) {
        this.role = surveyPromptDto.role();
        this.content = surveyPromptDto.content();
        this.isActive = surveyPromptDto.isActive();
    }
    /**
     * DTO로 Prompt Entity 생성
     *
     * @param dto role, 프롬프트 텍스트, 활성화
     * @return role, 프롬프트 텍스트, 활성화 기반 Prompt
     */
    public static Prompt createPrompt(SurveyPromptDto dto) {
        return Prompt.builder()
                .role(dto.role())
                .content(dto.content())
                .isActive(dto.isActive())
                .build();
    }

    /**
     * isActive 0으로 비활성화
     */
    public void deactivatePrompt() {
        this.isActive = 0;
    }
}
