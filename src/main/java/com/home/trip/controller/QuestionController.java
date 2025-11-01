package com.home.trip.controller;

import com.home.trip.domain.Question;
import com.home.trip.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "질문", description = "질문 관련 API")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionRepository questionRepository;

    @Operation(summary = "질문 가져오기", description = "질문 리스트를 order 오름차순 리턴")
    @GetMapping
    public List<Question> getQuestions() {
        return questionRepository.findAll(Sort.by(Sort.Direction.ASC, "order"));
    }
}
