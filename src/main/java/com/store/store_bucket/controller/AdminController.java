package com.store.store_bucket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin API", description = "관리자")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Operation(summary = "기록 조회", description = "모든 API 호출의 성공/실패 사유, 요청자, 일시를 리스트로 확인")
    @GetMapping("/log/history")
    public String getLogHistory() {
        return "기록 조회";
    }

    @Operation(summary = "재고 모니터링", description = "모든 상품의 현재 잔여 재고와 마지막 유효 주문 일시 확인")
    @GetMapping("/quantity/history")
    public String getQuantityHistory() {
        return "재고 모니터링";
    }
}
