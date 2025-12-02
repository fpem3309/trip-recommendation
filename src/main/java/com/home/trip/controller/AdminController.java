package com.home.trip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "관리자", description = "관리자 권한 필요")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Operation(summary = "메인 페이지", description = "관리자 메인 페이지")
    @GetMapping
    public ResponseEntity<String> home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("getAuthorities(): {}", authentication.getAuthorities());
        return ResponseEntity.ok(authentication.getAuthorities().toString());
    }
}
