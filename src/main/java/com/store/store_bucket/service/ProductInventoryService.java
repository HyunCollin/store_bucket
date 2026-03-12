package com.store.store_bucket.service;

import com.store.store_bucket.dto.ProductInventoryHistoryLastOrder;
import com.store.store_bucket.repository.ProductInventoryHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInventoryService {
    private final ProductInventoryHistoryRepository productInventoryHistoryRepository;
    /**
     * 재고 모니터링:
     *  모든 상품의 현재 잔여 재고와 마지막 유효 주문 일시 확인
     */
    public List<ProductInventoryHistoryLastOrder> getProductInventoryHistory(String actionType) {
        return productInventoryHistoryRepository.findProductInventoryHistoryLastOrders(actionType);
    }
}
