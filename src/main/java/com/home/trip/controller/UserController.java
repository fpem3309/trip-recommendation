package com.home.trip.controller;

import com.home.trip.domain.dto.AccessTokenResponse;
import com.home.trip.domain.dto.RecommendResponseDto;
import com.home.trip.domain.dto.SurveyDataResponseDto;
import com.home.trip.domain.dto.user.UserDto;
import com.home.trip.service.RefreshTokenService;
import com.home.trip.service.SurveyService;
import com.home.trip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원", description = "회원 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final SurveyService surveyService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 회원을 등록")
    public void joinUser(@RequestBody UserDto userDto) {
        userService.join(userDto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Access 토큰 재발급", description = "Refresh 토큰을 사용해 Access 토큰 재발급\n - 성공시 Access 토큰 리턴\n - 실패시 예외 발생")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@CookieValue(value = "refresh", required = false) String refreshToken) {
        log.info("요청 refresh: {}", refreshToken);
        String newAccessToken = refreshTokenService.refreshNewAccessToken(refreshToken);
        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
    }

    @DeleteMapping("/refresh")
    @Operation(summary = "Refresh 토큰 삭제", description = "테스트를 위해 Refresh 토큰을 삭제")
    public ResponseEntity<AccessTokenResponse> deleteRefreshToken(String userId) {
        String refreshToken = refreshTokenService.getRefreshToken(userId);
        refreshTokenService.deleteRefreshToken(userId);
        return ResponseEntity.ok(new AccessTokenResponse(refreshToken));
    }

    @Operation(summary = "회원의 설문 리스트", description = "로그인한 회원의 설문 리스트")
    @GetMapping("/survey")
    public ResponseEntity<List<RecommendResponseDto>> getSurveyList(Authentication auth) {
        String userId = (String) auth.getPrincipal();
        List<RecommendResponseDto> userSurveyList = surveyService.getUserSurveyReulstList(userId);
        return ResponseEntity.ok(userSurveyList);
    }

    @Operation(summary = "회원의 설문", description = "로그인한 회원의 설문 1개")
    @GetMapping("/survey/{surveyId}")
    public ResponseEntity<SurveyDataResponseDto> getSurvey(@PathVariable Long surveyId) {
        SurveyDataResponseDto surveyResult = surveyService.getSurveyResult(surveyId);
        return ResponseEntity.ok(surveyResult);
    }
}
