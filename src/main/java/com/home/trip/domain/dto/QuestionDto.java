package com.home.trip.domain.dto;

import com.home.trip.domain.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private String question;
    private List<Question.Option> options;
}
