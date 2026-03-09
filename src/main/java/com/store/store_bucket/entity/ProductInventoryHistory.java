package com.store.store_bucket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_no")
    private Long logNo;

    @Column(name = "inventory_no")
    private Long inventoryNo;

    @Column(name = "last_order_no")
    private Long lastOrderNo;

    @Column(name = "last_order_time")
    private LocalDateTime lastOrderTime;

    @Builder
    public ProductInventoryHistory(Long inventoryNo, Long lastOrderNo, LocalDateTime lastOrderTime) {
        this.inventoryNo = inventoryNo;
        this.lastOrderNo = lastOrderNo;
        this.lastOrderTime = lastOrderTime;
    }

}
