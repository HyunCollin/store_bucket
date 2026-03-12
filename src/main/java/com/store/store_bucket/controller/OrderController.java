package com.store.store_bucket.controller;

import com.store.store_bucket.dto.*;
import com.store.store_bucket.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order API", description = "주문 및 재고 관리 시스템")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class OrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(summary = "주문 API",
            description = "동시성 제어: 다수의 사용자가 동시에 주문할 때 발생하는 재고 경합 상황을 완벽히 처리해야 함 \n" +
                    "원자성 보장: 주문에 포함된 모든 상품의 재고가 확보될 때만 최종 승인")
    @PostMapping(value = "/order")
    public OrderProcess createOrder(@RequestBody OrderRequest orderRequest) {
        OrderProcess orderProcess = purchaseOrderService.saveTempOrder(orderRequest);
        if (orderProcess.isOrderAvailable()) {
            try {
                purchaseOrderService.purchaseOrder(orderProcess);
            } catch (Exception e) {
                // 주문 처리 중 예외 발생 시 주문 실패 처리
                purchaseOrderService.failPurchaseOrderProcess(orderProcess);
                orderProcess.fail();
            }
        } else {
            // 주문 요청 상품 검증 실패
            purchaseOrderService.failPurchaseOrderProcess(orderProcess);
        }
        return orderProcess;
    }

    @Operation(summary = "취소 API", description = "부분 취소: 주문 전체가 아닌 특정 상품의 수량만 취소하는 기능 지원 \n" +
            "정합성: 취소 승인과 동시에 해당 수량만큼 실시간 재고 복원")
    @PutMapping("/order/{orderId}/cancel")
    public CancelOrderProcess cancelOrderItem( @PathVariable Long orderId, @RequestBody CancelRequest cancelRequest) {
        String userId = cancelRequest.getUserId();
        CancelOrderProcess cancelOrderProcess;
        try {
            cancelOrderProcess = purchaseOrderService.getCancelOrderProcess(orderId, userId, cancelRequest.getCancelOrderItems());
            return cancelOrderProcess;
        } catch (Exception e) {
            // TODO - 취소 처리 중 예외 발생 시 취소 실패 처리
            cancelOrderProcess = CancelOrderProcess.builder().build();
            cancelOrderProcess.fail();
        }
        return cancelOrderProcess;
    }

    @Operation(summary = "주문 내역 조회 API", description = "정렬 및 페이징: 최근 주문 순으로 정렬하며, 주문 건수 기준 5개씩 페이징 처리 \n" +
            "상세 노출:  주문 상품 리스트와 각 상품별 취소 내역(취소 수량, 일시 등)을 모두 포함")
    @GetMapping(value = "/order")
    public ViewOrderPage getOrders(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageCount) {
        String userId = "test1";
        return purchaseOrderService.getOrderListByUserId(userId, pageNum, pageCount);
    }
}