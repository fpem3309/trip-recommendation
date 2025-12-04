package com.home.trip;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Trip Recommendation API")
                        .description("여행 추천 서비스 API 명세서")
                        .version("v1.0.0"));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // 1. Request Body 정의
            Schema<?> loginSchema = new Schema<>()
                    .type("object")
                    .addProperty("userId", new Schema<>().type("string").example("test"))
                    .addProperty("password", new Schema<>().type("string").format("password").example("test123"));

            RequestBody requestBody = new RequestBody()
                    .description("로그인 정보")
                    .required(true)
                    .content(new Content()
                            .addMediaType("application/json", new MediaType().schema(loginSchema)));

            // 2. Responses 정의
            ApiResponses apiResponses = new ApiResponses()
                    .addApiResponse("200", new ApiResponse()
                            .description("로그인 성공 및 JWT accessToken 리턴")
                            .content(new Content().addMediaType("application/json", new MediaType()
                                    .schema(new Schema<>().type("object")
                                            .addProperty("token", new Schema<>().type("string"))))))
                    .addApiResponse("401", new ApiResponse().description("인증 실패"));

            // 3. Operation 정의
            Operation operation = new Operation()
                    .summary("로그인")
                    .description("아이디와 비밀번호로 로그인\n - JWT Access Token과 Refresh Token을 발급\n - refresh 토큰은 Redis에 저장")
                    .tags(Collections.singletonList("회원"))
                    .requestBody(requestBody)
                    .responses(apiResponses);

            // 4. PathItem에 Operation 추가
            PathItem pathItem = new PathItem().post(operation);

            // 5. OpenAPI 객체에 Path 추가
            openApi.getPaths().addPathItem("/api/auth/login", pathItem);
        };
    }
}
