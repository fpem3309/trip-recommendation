package com.home.trip.domain.dto.question;

import com.home.trip.domain.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private String question;
    private Long questionId;
    private List<Question.Option> options;
    private Long order;
}
