package com.store.store_bucket.dto;

import com.store.store_bucket.enums.ItemStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ViewOrderItem {

    private Long orderItemNo;
    private Long orderNo;
    private String productId;
    private Long inventoryNo;
    private String color;
    private String size;
    private Integer orderQuantity;
    private Integer cancelQuantity;
    private ItemStatus itemStatus;
    private LocalDateTime createdAt;
    private LocalDateTime cancelAt;
}
