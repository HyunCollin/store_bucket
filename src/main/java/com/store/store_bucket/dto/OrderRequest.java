package com.store.store_bucket.dto;

import com.store.store_bucket.enums.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String userId;
    private OrderStatus orderStatus;
    List<PurchaseProductDto> purchaseProducts;
}
