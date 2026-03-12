package com.store.store_bucket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class QuantityHistory {
    private List<ProductInventoryHistoryLastOrder> productInventoryHistoryLastOrders;
}
