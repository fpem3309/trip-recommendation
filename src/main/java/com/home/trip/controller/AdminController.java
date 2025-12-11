package com.home.trip.controller;

import com.home.trip.domain.Question;
import com.home.trip.domain.User;
import com.home.trip.domain.dto.UserDto;
import com.home.trip.repository.QuestionRepository;
import com.home.trip.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "관리자", description = "관리자 권한 필요")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

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
        List<Question> questions = questionRepository.findAll(Sort.by(Sort.Direction.ASC, "order"));
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "회원 관리 페이지", description = "회원 리스트 및 페이징")
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        Page<UserDto> userList = page.map(user -> new UserDto(user.getUserId(),
                        user.getPassword(), user.getEmail(), user.getNickname(), user.getRoles(),
                        user.getOauth_provider(), user.getCreatedAt(), user.getUpdatedAt()));
        return ResponseEntity.ok(userList);
    }
}
