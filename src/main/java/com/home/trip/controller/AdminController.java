package com.home.trip.controller;

import com.home.trip.domain.Question;
import com.home.trip.domain.dto.user.UserDto;
import com.home.trip.domain.dto.question.QuestionDto;
import com.home.trip.domain.dto.question.QuestionOrderUpdateDto;
import com.home.trip.domain.dto.user.UserUpdateDto;
import com.home.trip.service.QuestionService;
import com.home.trip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "관리자", description = "관리자 권한 필요")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final QuestionService questionService;
    private final UserService userService;

    @Operation(summary = "메인 페이지", description = "관리자 메인 페이지")
    @GetMapping
    public ResponseEntity<String> home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("getAuthorities(): {}", authentication.getAuthorities());
        return ResponseEntity.ok(authentication.getAuthorities().toString());
    }

    @Operation(summary = "질문 관리 페이지", description = "관리자 질문 수정 등 관리 페이지")
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions() {
        List<Question> questions = questionService.findAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "질문 추가", description = "질문 리스트에 질문 추가하기")
    @PostMapping("/questions")
    public ResponseEntity<String> addQuestion(@RequestBody QuestionDto questionDto) {
        questionService.insertQuestion(questionDto);
        return ResponseEntity.ok("insert");
    }

    @Operation(summary = "질문 삭제", description = "해당 id(_id) 질문 1개 삭제")
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("deleted " + id);
    }

    @Operation(summary = "질문 수정", description = "해당 id(_id) 질문 옵션을 수정하거나 삭제")
    @PutMapping("/questions/{id}")
    public ResponseEntity<String> updateQuestion(@PathVariable String id, @RequestBody QuestionDto questionDto) {
        questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok("updated " + id);
    }

    @Operation(summary = "질문 정렬 수정", description = "질문 리스트의 정렬 순서를 수정")
    @PatchMapping("/questions/order")
    public ResponseEntity<String> updateQuestionsOrder(@RequestBody QuestionOrderUpdateDto request) {
        questionService.updateQuestionOrder(request);
        return ResponseEntity.ok("updated");
    }

    @Operation(summary = "회원 관리 페이지", description = "회원 리스트 및 페이징")
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getUsers(Pageable pageable) {
        Page<UserDto> userList = userService.findAllUsers(pageable);
        return ResponseEntity.ok(userList);
    }

    @Operation(summary = "회원 수정", description = "해당 userId로 회원 정보를 수정")
    @PutMapping("/users")
    public ResponseEntity<String> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userUpdateDto);
        return ResponseEntity.ok("updated " + userUpdateDto.getUserId());
    }

    @Operation(summary = "회원 삭제", description = "해당 userId로 회원 삭제")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("deleted " + userId);
    }
}
