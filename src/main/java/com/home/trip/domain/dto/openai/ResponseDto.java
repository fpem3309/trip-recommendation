package com.home.trip.domain.dto.openai;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDto {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private MessageDto message;
    }
}
