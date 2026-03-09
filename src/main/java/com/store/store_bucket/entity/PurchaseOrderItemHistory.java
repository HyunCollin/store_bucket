package com.store.store_bucket.entity;

import com.store.store_bucket.enums.ActionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order_item_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrderItemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_no")
    private Long historyNo;

    @Column(name = "order_no", nullable = false, updatable = false)
    private Long orderNo;

    @Column(name = "order_item_no", updatable = false)
    private Long orderItemNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20, updatable = false)
    private ActionType actionType;

    @Column(name = "changed_quantity", nullable = false, updatable = false)
    private Integer changedQuantity;

    @Column(name = "reason", length = 255, updatable = false)
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PurchaseOrderItemHistory(Long orderNo, Long orderItemNo, ActionType actionType,
                                    Integer changedQuantity, String reason) {
        this.orderNo = orderNo;
        this.orderItemNo = orderItemNo;
        this.actionType = actionType;
        this.changedQuantity = changedQuantity;
        this.reason = reason;
    }
}
