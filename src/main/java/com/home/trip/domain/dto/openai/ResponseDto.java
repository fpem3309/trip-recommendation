package com.home.trip.domain.dto.openai;

import java.util.List;

public record ResponseDto(List<Choice> choices) {
    public record Choice(int index, MessageDto message) {
    }
}
