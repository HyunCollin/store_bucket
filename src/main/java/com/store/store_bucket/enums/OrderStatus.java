package com.store.store_bucket.enums;

public enum OrderStatus {
    PENDING,    // 결제/재고확인 대기
    COMPLETED,  // 주문 완료
    FAILED,     // 주문 실패
    CANCELLED   // 주문 취소
}
