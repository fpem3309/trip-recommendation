package com.home.trip.domain.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private String model;
    private List<MessageDto> messages;
    private double temperature;
}
