package com.store.store_bucket.enums;

public enum ItemStatus {
    PENDING,    // 최조 주문 생성
    CANCELLED,  // 취소 완료 상태 (전체 수량 취소 시)
    FAILED,     // 주문 실패
    COMPLETED   // 주문 완료 (재고 차감 완료)
}
