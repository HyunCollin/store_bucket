package com.store.store_bucket.entity;

import com.store.store_bucket.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @CreationTimestamp // INSERT 시 현재 시간 자동 입력
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PurchaseOrder(String userId, OrderStatus orderStatus) {
        this.userId = userId;
        this.orderStatus = orderStatus;
    }

    // --- 비즈니스 로직 ---

    /**
     * 주문 상태 변경 (PENDING -> COMPLETED 등)
     */
    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }

    public void fail() {
        this.orderStatus = OrderStatus.FAILED;
    }

    public void completed() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public String getCreatedAtString() {
        if (this.createdAt == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.createdAt.format(formatter);
    }
}
