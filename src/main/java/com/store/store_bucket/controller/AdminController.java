package com.store.store_bucket.controller;

import com.store.store_bucket.dto.ProductInventoryHistoryLastOrder;
import com.store.store_bucket.dto.QuantityHistory;
import com.store.store_bucket.service.ProductInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin API", description = "관리자")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProductInventoryService productInventoryService;

    @Operation(summary = "기록 조회", description = "모든 API 호출의 성공/실패 사유, 요청자, 일시를 리스트로 확인")
    @GetMapping("/log/history")
    public String getLogHistory() {
        return "기록 조회";
    }

    @Operation(summary = "재고 모니터링", description = "모든 상품의 현재 잔여 재고와 마지막 유효 주문 일시 확인")
    @Parameter(name = "actionType", description = "조회할 기록의 유형 (예: '', 'ORDER', 'CANCEL')")
    @GetMapping("/quantity/history")
    public QuantityHistory getQuantityHistory(@RequestParam(required = false, defaultValue = "") String actionType) {
        try {
            List<ProductInventoryHistoryLastOrder> inventoryHistory = productInventoryService.getProductInventoryHistory(actionType);
            return QuantityHistory.builder()
                    .productInventoryHistoryLastOrders(inventoryHistory)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return QuantityHistory.builder()
                    .productInventoryHistoryLastOrders(null)
                    .build();
        }
    }
}
