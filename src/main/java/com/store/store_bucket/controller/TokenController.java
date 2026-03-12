package com.store.store_bucket.controller;

import com.store.store_bucket.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token API", description = "Token 발급 ")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @Operation(summary = "토큰 발급", description = "user_id를 받아 JWT를 생성합니다. 유효 시간 10분")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements // swagger 에서 인증 필요 없는 API로 설정
    @PostMapping("/token/issue")
    public String issueToken(@RequestParam(required = true) String userId) {
        return tokenService.createToken(userId);
    }

}
