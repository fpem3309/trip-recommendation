package com.home.trip.controller;

import com.home.trip.domain.Question;
import com.home.trip.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;

    @GetMapping
    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }
}
