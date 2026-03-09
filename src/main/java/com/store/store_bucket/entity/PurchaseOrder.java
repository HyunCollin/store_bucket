package com.store.store_bucket.entity;

import com.store.store_bucket.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no")
    private Long orderNo;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Enumerated(EnumType.STRING) // 문자열 그대로 DB에 저장 (권장)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @CreationTimestamp // INSERT 시 현재 시간 자동 입력
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PurchaseOrder(String userId, OrderStatus orderStatus, Integer totalQuantity) {
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.totalQuantity = totalQuantity;
    }

    // --- 비즈니스 로직 ---

    /**
     * 주문 상태 변경 (PENDING -> COMPLETED 등)
     */
    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }
}
