package com.store.store_bucket.repository;

import com.store.store_bucket.dto.ProductInventoryHistoryLastOrder;

import java.util.List;

public interface ProductInventoryHistoryRepositoryCustom {
    /**
     * 모든 상품의 현재 잔여 재고와 마지막 유효 주문 일시 확인
     */
    List<ProductInventoryHistoryLastOrder> findProductInventoryHistoryLastOrders(String actionType);
}
