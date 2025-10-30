package com.home.trip.domain;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor
@Document(collection = "questions")
public class Question {
    @Id
    private String id;
    private String question;
    private List<Option> options;

    @Getter
    @NoArgsConstructor
    public static class Option {
        private String label;
        private String value;
    }
}
