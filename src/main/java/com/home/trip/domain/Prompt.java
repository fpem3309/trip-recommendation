package com.home.trip.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "prompts")
public class Prompt {
    @Id
    private String id;
    private String role;
    private String content;
}
