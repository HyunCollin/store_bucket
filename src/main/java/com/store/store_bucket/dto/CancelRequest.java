package com.store.store_bucket.dto;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequest {
    private String userId;
    private Long orderNo;
    private HashMap<Long, CancelOrderItem> cancelOrderItems;
}
