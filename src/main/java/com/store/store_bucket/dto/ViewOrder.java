package com.store.store_bucket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class ViewOrder {
    private Long orderNo;
    private String userId;
    private String orderStatus;

    private List<ViewOrderItem> viewOrderItems;
    private String createdAt;
}
