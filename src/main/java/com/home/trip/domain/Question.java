package com.home.trip.domain;

import com.home.trip.domain.dto.QuestionDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "questions")
public class Question {
    @Id
    private String id;
    private String question;
    private List<Option> options;
    private Long order;

    @Getter
    @NoArgsConstructor
    public static class Option {
        private String label;
        private String value;
    }

    public static Question createQuestion(QuestionDto dto) {
        return Question.builder()
                .question(dto.getQuestion())
                .options(dto.getOptions())
                .order(dto.getOrder())
                .build();
    }

    public Question changeQuestionOrOption(String id, QuestionDto dto) {
        return Question.builder()
                .id(id)
                .question(dto.getQuestion())
                .options(dto.getOptions())
                .order(dto.getOrder())
                .build();
    }
}
