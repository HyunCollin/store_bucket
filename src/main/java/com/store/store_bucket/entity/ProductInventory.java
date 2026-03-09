package com.store.store_bucket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_no")
    private Long inventoryNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_no", nullable = false)
    private Product product;

    @Column(name = "color", nullable = false, length = 20)
    private String color;

    @Column(name = "size", nullable = false, length = 20)
    private String size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Builder
    public ProductInventory(Product product, String color, String size, Integer quantity) {
        this.product = product;
        this.color = color;
        this.size = size;
        this.quantity = (quantity != null) ? quantity : 0;
    }

    /**
     * 재고 차감
     */
    public void decreaseQuantity(int amount) {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고가 부족합니다. (현재: " + this.quantity + ")");
        }
        this.quantity -= amount;
    }

    /**
     * 재고 추가
     */
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}
