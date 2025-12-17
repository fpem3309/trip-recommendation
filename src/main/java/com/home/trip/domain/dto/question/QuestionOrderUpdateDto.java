package com.home.trip.domain.dto.question;

import lombok.Data;

import java.util.List;

@Data
public class QuestionOrderUpdateDto {
    private List<QuestionOrderDto> questions;
}
