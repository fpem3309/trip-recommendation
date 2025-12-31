package com.home.trip.domain.dto.question;

import lombok.Data;

import java.util.List;

public record QuestionOrderUpdateDto(List<QuestionOrderDto> questions) {
}
