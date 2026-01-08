package com.home.trip.service;

import com.home.trip.domain.Prompt;
import com.home.trip.domain.dto.openai.SurveyPromptDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OpenAiServiceTest {

    @Autowired
    OpenAiService openAiService;

    @Test
    void 프롬프트_검색() throws Exception {
        // given
        String searchId = "6908ab576899743a45888b5e";

        // when
        Optional<Prompt> prompt = openAiService.findPromptById(searchId);

        // then
        Assertions.assertThat(prompt).isPresent();
        Assertions.assertThat(prompt).isNotEmpty();
    }

    @Test
    void 프롬프트_검색_실패() throws Exception {
        // given
        String searchId = "없는 아이디";

        // when
        Optional<Prompt> prompt = openAiService.findPromptById(searchId);

        // then
        Assertions.assertThat(prompt).isNotPresent();
        Assertions.assertThat(prompt).isEmpty();
    }

    @Test
    void 활성화된_프롬프트_찾기() throws Exception {
        // given
        String role = "system";

        // when
        Optional<Prompt> prompt = openAiService.findActivePromptByRole(role);

        // then
        Assertions.assertThat(prompt).isPresent();
        Assertions.assertThat(prompt).isNotEmpty();
        Assertions.assertThat(prompt.get().getIsActive()).isEqualTo(1);
    }

    @Test
    void 없는_role로_활성화된_프롬프트_찾기_실패() throws Exception {
        // given
        String role = "없는 role";

        // when
        Optional<Prompt> prompt = openAiService.findActivePromptByRole(role);

        // then
        Assertions.assertThat(prompt).isNotPresent();
        Assertions.assertThat(prompt).isEmpty();
    }

    @Test
    void 있는_role이지만_비활성화된_프롬프트를_찾아서_실패() throws Exception {
        // given
        String role = "test";

        // when
        Optional<Prompt> prompt = openAiService.findActivePromptByRole(role);

        // then
        Assertions.assertThat(prompt).isNotPresent();
        Assertions.assertThat(prompt).isEmpty();
    }

    @Test
    void 모든_프롬프트_리스트() throws Exception {
        // given
        List<SurveyPromptDto> allPrompts = openAiService.findAllPrompts();

        // when
        int size = allPrompts.size();

        // then
        Assertions.assertThat(size).isGreaterThanOrEqualTo(1);
    }

    @Test
    void 비활성화_프롬프트_새로_저장() throws Exception {
        // given
        SurveyPromptDto dto = createSurveyPromptDto(0);

        // when
        String savedId = openAiService.savePrompt(dto);
        Optional<Prompt> prompt = openAiService.findPromptById(savedId);

        // then
        Assertions.assertThat(prompt).isPresent();
        Assertions.assertThat(prompt).isNotEmpty();
        Assertions.assertThat(prompt.get().getIsActive()).isEqualTo(0);
    }

    @Test
    void 활성화_프롬프트_새로_저장하면_이전_활성화_프롬프트는_비활성화() throws Exception {
        // given
        SurveyPromptDto dto = createSurveyPromptDto(1);
        String role = "system";
        String beforeActivePromptId = openAiService.findActivePromptByRole(role).get().getId();

        // when
        String savedId = openAiService.savePrompt(dto);
        Optional<Prompt> prompt = openAiService.findPromptById(savedId);
        Optional<Prompt> beforeActivePrompt = openAiService.findPromptById(beforeActivePromptId);
        Optional<Prompt> afterActivePrompt = openAiService.findActivePromptByRole(role);

        // then
        Assertions.assertThat(prompt).isPresent();
        Assertions.assertThat(prompt).isNotEmpty();
        Assertions.assertThat(prompt.get().getIsActive()).isEqualTo(1);
        Assertions.assertThat(prompt.get().getId()).isEqualTo(afterActivePrompt.get().getId());

        Assertions.assertThat(beforeActivePrompt).isPresent();
        Assertions.assertThat(beforeActivePrompt).isNotEmpty();
        Assertions.assertThat(beforeActivePrompt.get().getIsActive()).isZero();
        Assertions.assertThat(beforeActivePrompt.get().getIsActive()).isEqualTo(0);
    }

    @Test
    void 비활성화_프롬프트_업데이트() throws Exception {
        // given
        SurveyPromptDto dto = createSurveyPromptDto(0);
        String savedId = openAiService.savePrompt(dto);
        SurveyPromptDto updateDto = updateSurveyPromptDto(0);

        // when
        String updatedId = openAiService.updatePrompt(savedId, updateDto);
        Optional<Prompt> prompt = openAiService.findPromptById(updatedId);

        // then
        Assertions.assertThat(savedId).isEqualTo(updatedId);
        Assertions.assertThat(prompt).isPresent();
        Assertions.assertThat(prompt).isNotEmpty();
        Assertions.assertThat(prompt.get().getContent()).isEqualTo("update content");
        Assertions.assertThat(prompt.get().getIsActive()).isZero();
        Assertions.assertThat(prompt.get().getIsActive()).isEqualTo(0);
    }

    @Test
    void 활성화_프롬프트_업데이트() throws Exception {
        // given
        SurveyPromptDto dto = createSurveyPromptDto(0);
        String savedId = openAiService.savePrompt(dto);
        SurveyPromptDto updateDto = updateSurveyPromptDto(1);
        String role = "system";
        String beforeActivePromptId = openAiService.findActivePromptByRole(role).get().getId();

        // when
        String updatedId = openAiService.updatePrompt(savedId, updateDto);
        Optional<Prompt> prompt = openAiService.findPromptById(updatedId);
        Optional<Prompt> beforeActivePrompt = openAiService.findPromptById(beforeActivePromptId);
        Optional<Prompt> afterActivePrompt = openAiService.findActivePromptByRole(role);

        // then
        Assertions.assertThat(savedId).isEqualTo(updatedId);
        Assertions.assertThat(prompt).isPresent();
        Assertions.assertThat(prompt).isNotEmpty();
        Assertions.assertThat(prompt.get().getContent()).isEqualTo("update content");
        Assertions.assertThat(prompt.get().getIsActive()).isNotZero();
        Assertions.assertThat(prompt.get().getIsActive()).isEqualTo(1);

        Assertions.assertThat(beforeActivePrompt).isPresent();
        Assertions.assertThat(beforeActivePrompt).isNotEmpty();
        Assertions.assertThat(beforeActivePrompt.get().getIsActive()).isZero();
        Assertions.assertThat(beforeActivePrompt.get().getIsActive()).isEqualTo(0);

        Assertions.assertThat(afterActivePrompt).isPresent();
        Assertions.assertThat(afterActivePrompt.get().getId()).isEqualTo(updatedId);
        Assertions.assertThat(afterActivePrompt.get().getContent()).isEqualTo("update content");
    }

    @Test
    void 프롬프트_삭제() throws Exception {
        // given
        SurveyPromptDto dto = createSurveyPromptDto(0);
        String savedId = openAiService.savePrompt(dto);

        // when
        String deletedId = openAiService.deletePrompt(savedId);
        Optional<Prompt> prompt = openAiService.findPromptById(deletedId);

        // then
        Assertions.assertThat(prompt).isNotPresent();
        Assertions.assertThat(prompt).isEmpty();
    }

    private static SurveyPromptDto createSurveyPromptDto(int isActive) {
        Prompt prompt = new Prompt("1", "system", "content", isActive);
        return SurveyPromptDto.createSurveyPromptDto(prompt);
    }

    private static SurveyPromptDto updateSurveyPromptDto(int isActive) {
        Prompt prompt = new Prompt("1", "system", "update content", isActive);
        return SurveyPromptDto.createSurveyPromptDto(prompt);
    }

}