package com.store.store_bucket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ViewOrderPage {
    private int pageNo;
    private int pageCount;
    private int totalPages;
    boolean hasNextPage;
    boolean hasPreviousPage;

    private List<ViewOrder> viewOrders;
}
