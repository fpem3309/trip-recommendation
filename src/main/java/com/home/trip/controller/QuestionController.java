package com.home.trip.controller;

import com.home.trip.domain.Question;
import com.home.trip.domain.dto.QuestionDto;
import com.home.trip.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "질문 추가", description = "질문 리스트에 질문 추가하기")
    @PostMapping
    public ResponseEntity<String> addQuestion(@RequestBody QuestionDto questionDto) {
        Long count = questionRepository.count();
        questionDto.setOrder(count);
        Question question = Question.createQuestion(questionDto);
        questionRepository.insert(question);
        return ResponseEntity.ok("insert");
    }

    @Operation(summary = "질문 삭제", description = "해당 id(_id) 질문 1개 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 질문이 없습니다.");
        }
        questionRepository.deleteById(id);
        return ResponseEntity.ok("deleted " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> patchQuestion(@PathVariable String id, @RequestBody QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        Question changeQuestion = question.changeQuestionOrOption(id, questionDto);
        questionRepository.save(changeQuestion);
        return ResponseEntity.ok("changed " + changeQuestion.getId());
    }
}
