package com.home.trip.domain.dto.openai;

import lombok.Builder;

import java.util.List;

@Builder
public record RequestDto(String model, List<MessageDto> messages, double temperature) {
}
