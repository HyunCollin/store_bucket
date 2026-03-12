package com.store.store_bucket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryHistoryLastOrder {
    private String productId;
    private String color;
    private String size;
    private Integer currentQuantity;
    private Long lastOrderNo;
    private LocalDateTime lastOrderTime;
}
