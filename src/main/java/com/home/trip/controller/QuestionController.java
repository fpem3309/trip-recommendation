package com.home.trip.controller;

import com.home.trip.domain.Question;
import com.home.trip.domain.dto.question.QuestionDto;
import com.home.trip.domain.dto.question.QuestionOrderDto;
import com.home.trip.domain.dto.question.QuestionOrderUpdateDto;
import com.home.trip.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

    @Operation(summary = "질문 수정", description = "해당 id(_id) 질문 옵션을 수정하거나 삭제")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateQuestion(@PathVariable String id, @RequestBody QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        Question changeQuestion = question.changeQuestionOrOption(id, questionDto);
        questionRepository.save(changeQuestion);
        return ResponseEntity.ok("updated " + changeQuestion.getId());
    }

    @Operation(summary = "질문 정렬 수정", description = "질문 리스트의 정렬 순서를 수정")
    @PatchMapping("/order")
    public ResponseEntity<String> updateQuestionsOrder(@RequestBody QuestionOrderUpdateDto request) {
        //TODO: 성능 개선 필요

        // 1. id 목록 추출
        List<String> ids = request.getQuestions().stream()
                .map(QuestionOrderDto::getId)
                .toList();

        // 2. 기존 질문 조회
        List<Question> questions = questionRepository.findAllById(ids);

        // 3. id → order 매핑
        Map<String, Long> orderMap = request.getQuestions().stream()
                .collect(Collectors.toMap(
                        QuestionOrderDto::getId,
                        QuestionOrderDto::getOrder
                ));

        // 4. order만 수정
        questions.forEach(q -> {
            Long newOrder = orderMap.get(q.getId()); // 해당 Id는 수정된 order가 저장
            q.changeOrder(newOrder); // 수정된 order로 변경
        });

        questionRepository.saveAll(questions);
        return ResponseEntity.ok("updated");
    }
}
