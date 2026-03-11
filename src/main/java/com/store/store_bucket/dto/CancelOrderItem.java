package com.store.store_bucket.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderItem {

    /**
     * 취소 주문 상품 번호
     */
    private Long orderItemNo;
    /**
     * 주문 취소 수량
     */
    private Integer cancelQuantity;
}
