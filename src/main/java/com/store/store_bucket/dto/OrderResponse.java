package com.store.store_bucket.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private boolean isOrderSuccess;

    List<PurchaseProductDto> failProducts;

    public void fail(PurchaseProductDto purchaseProductDto) {
        if (this.failProducts == null) {
            this.failProducts = new ArrayList<>();
        }
        PurchaseProductDto failProduct = purchaseProductDto.toBuilder().build();
        this.failProducts.add(failProduct);
        this.isOrderSuccess = false;
    }

    public void successOrder() {
        this.isOrderSuccess = true;
    }
}
