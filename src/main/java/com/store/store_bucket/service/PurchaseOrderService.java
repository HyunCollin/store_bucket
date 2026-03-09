package com.store.store_bucket.service;

import com.store.store_bucket.dto.OrderRequest;
import com.store.store_bucket.dto.OrderResponse;
import com.store.store_bucket.dto.PurchaseProductDto;
import com.store.store_bucket.entity.ProductInventory;
import com.store.store_bucket.entity.PurchaseOrder;
import com.store.store_bucket.entity.PurchaseOrderItem;
import com.store.store_bucket.enums.OrderStatus;
import com.store.store_bucket.repository.ProductInventoryRepository;
import com.store.store_bucket.repository.ProductRepository;
import com.store.store_bucket.repository.PurchaseOrderItemRepository;
import com.store.store_bucket.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        OrderResponse orderResponse = OrderResponse.builder().build();
        // TODO 주문 생성 ( 대기 상태 )
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .userId(orderRequest.getUserId())
                .orderStatus(OrderStatus.PENDING)
                .build();
        purchaseOrderRepository.save(purchaseOrder);

        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        boolean isOrderFail = false;
        for (PurchaseProductDto requestProduct : orderRequest.getPurchaseProducts()) {
        // 구매 상품 재고 조회
            ProductInventory dbProductInventory =
                    productInventoryRepository.findByProductProductIdAndColorAndSize(requestProduct.getProductId(), requestProduct.getColor(), requestProduct.getSize());
            String snapshotInfo = String.format("%s_%s",requestProduct.getColor(), requestProduct.getSize());
            PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .productInventory(dbProductInventory)
                    .orderQuantity(requestProduct.getQuantity())
                    .snapshotInfo(snapshotInfo)
                    .build();

            if (dbProductInventory == null) {
                // 주문 상품 재고 조회 실패
                isOrderFail = true;
                String reason = String.format("주문 상품 재고 확인 불가 %s %s %s %s", requestProduct.getProductId(), requestProduct.getColor(), requestProduct.getSize(), requestProduct.getQuantity());
                log.error(reason);
                orderResponse.fail(requestProduct);
                purchaseOrderItem.fail();
            } else {
                // 주문 상품 재고 검증
                if (dbProductInventory.getQuantity() >= requestProduct.getQuantity()) {
                    // TODO 통과 -> 재고 차감 O -> 주문 완료 상태로 변경
                    // 주문 가능
                    dbProductInventory.decreaseQuantity(requestProduct.getQuantity());
                    purchaseOrderItem.successOrder();
                    continue;
                } else {
                    // 실패 -> 재고 차감 X -> 주문 실패 상태로 변경
                    // 주문 불가 재고 부족
                    isOrderFail = true;
                    purchaseOrderItem.fail();
                    orderResponse.fail(requestProduct);
                }
            }
            purchaseOrderItems.add(purchaseOrderItem);
        }

        // 구매 상품 재고 없음
        if (isOrderFail) {
            // TODO 주문 상품 정보 없음 -> 주문 실패 저장
            // TODO 주문 생성 응답 객체 생성하고, 반환
                    // TODO dbProductInventory 차감 했던 재고 원복 하기
            // 주문 실패 생성, api 로그 생성
            purchaseOrder.fail();
            purchaseOrderRepository.save(purchaseOrder);
            purchaseOrderItemRepository.saveAll(purchaseOrderItems);

            return orderResponse;
        }

        purchaseOrder.completed();
        purchaseOrderRepository.save(purchaseOrder);
        purchaseOrderItemRepository.saveAll(purchaseOrderItems);

        orderResponse.successOrder();
        return orderResponse;

    }
}
