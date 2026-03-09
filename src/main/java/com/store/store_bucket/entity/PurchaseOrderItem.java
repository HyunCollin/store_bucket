package com.store.store_bucket.entity;

import com.store.store_bucket.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_no")
    private Long orderItemNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_no", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_no", nullable = false)
    private ProductInventory productInventory;

    @Column(name = "order_quantity", nullable = false)
    private Integer orderQuantity;

    @Column(name = "cancel_quantity")
    private Integer cancelQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", length = 20)
    private ItemStatus itemStatus = ItemStatus.ORDERED;

    @Column(name = "snapshot_info")
    private String snapshotInfo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancel_at")
    private LocalDateTime cancelAt;

    @Builder
    public PurchaseOrderItem(PurchaseOrder purchaseOrder, ProductInventory productInventory, Integer orderQuantity, String snapshotInfo) {
        this.purchaseOrder = purchaseOrder;
        this.productInventory = productInventory;
        this.orderQuantity = orderQuantity;
        this.snapshotInfo = snapshotInfo;
        this.cancelQuantity = 0;
        this.itemStatus = ItemStatus.ORDERED;
    }

    // --- 비즈니스 로직 ---

    /**
     * 부분 취소 처리
     * @param amount 취소할 수량
     */
    public void cancel(int amount) {
        int remainingQuantity = this.orderQuantity - this.cancelQuantity;

        if (amount <= 0) {
            throw new IllegalArgumentException("취소 수량은 0보다 커야 합니다.");
        }
        if (amount > remainingQuantity) {
            throw new IllegalArgumentException("취소 가능 수량을 초과했습니다. (가능: " + remainingQuantity + ")");
        }

        this.cancelQuantity += amount;
        this.cancelAt = LocalDateTime.now();

        // 전체 수량이 취소되었다면 상태 변경
        if (this.cancelQuantity.equals(this.orderQuantity)) {
            this.itemStatus = ItemStatus.CANCELLED;
        }
    }
}
