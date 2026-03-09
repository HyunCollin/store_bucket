package com.store.store_bucket.service;

import com.store.store_bucket.dto.OrderRequest;
import com.store.store_bucket.dto.PurchaseProductDto;
import com.store.store_bucket.entity.ProductInventory;
import com.store.store_bucket.repository.ProductInventoryRepository;
import com.store.store_bucket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public void createOrder(OrderRequest orderRequest) {
        // TODO 구매 상품 재고 조회
        boolean isOrderFail = false;
        String reason = null;
        HashMap<String, ProductInventory> orderProducts = new HashMap<>();
        for (PurchaseProductDto product : orderRequest.getPurchaseProducts()) {
            ProductInventory productInventory =
                    productInventoryRepository.findByProductProductIdAndColorAndSizeAndQuantityGreaterThanEqual(product.getProductId(), product.getColor(), product.getSize(), product.getQuantity());
            orderProducts.put(product.getProductId(), productInventory);
            if (productInventory == null) {
                isOrderFail = true;
                reason = String.format("주문 상품 재고 확인 불가 %s %s %s %s", product.getProductId(), product.getColor(), product.getSize(), product.getQuantity());
            }
        }

        if (isOrderFail) {
            // TODO 주문 상품 정보 없음 -> 주문 실패 저장
            log.error(reason);
            // TODO 주문 생성 응답 객체 생성하고, 반환
            // 주문 실패 생성, api 로그 생성
            return;
        }

        // TODO 주문 생성 ( 대기 상태 )
        // TODO 주문 상품 재고 검증
        // TODO 통과 -> 재고 차감 O -> 주문 완료 상태로 변경
        // TODO 실패 -> 재고 차감 X -> 주문 실패 상태로 변경

    }
}
