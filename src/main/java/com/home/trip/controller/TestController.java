package com.home.trip.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Tag(name = "테스트 API", description = "Swagger UI 테스트용")

public class TestController {

    @GetMapping("/get-test/{path}")
    @Operation(summary = "GET 테스트", description = " \"path + param\" 문자열을 반환합니다")
    public String getTest(@PathVariable("path") String path, @RequestParam("param") String param) {
        return path + param;
    }

    @PostMapping("/post-test")
    @Operation(summary = "POST 테스트", description = "두 숫자가 같은지 여부를 반환합니다")
    public Boolean postTest(@RequestParam int num1, @RequestParam int num2) {
        return num1 == num2;
    }
}
