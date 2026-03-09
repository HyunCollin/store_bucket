package com.store.store_bucket.dto;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseProductDto {
    private String productId;
    private String color;
    private String size;
    private Integer quantity;
}
