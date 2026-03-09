package com.store.store_bucket.enums;

public enum ItemStatus {
    ORDERED,    // 주문 완료 상태
    CANCELLED,  // 취소 완료 상태 (전체 수량 취소 시)
    FAILED     // 주문 실패
}
