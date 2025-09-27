package com.home.trip.domain.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDto {
    private String role;  // "system" | "user" | "assistant"
    private String content;
}
