package com.store.store_bucket.service;

import com.store.store_bucket.dto.*;
import com.store.store_bucket.entity.ProductInventory;
import com.store.store_bucket.entity.PurchaseOrder;
import com.store.store_bucket.entity.PurchaseOrderItem;
import com.store.store_bucket.entity.PurchaseOrderItemHistory;
import com.store.store_bucket.enums.ActionType;
import com.store.store_bucket.enums.OrderStatus;
import com.store.store_bucket.repository.ProductInventoryRepository;
import com.store.store_bucket.repository.PurchaseOrderItemHistoryRepository;
import com.store.store_bucket.repository.PurchaseOrderItemRepository;
import com.store.store_bucket.repository.PurchaseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final ProductInventoryRepository productInventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final PurchaseOrderItemHistoryRepository purchaseOrderItemHistoryRepository;

    @Transactional
    public OrderProcess saveTempOrder(OrderRequest orderRequest) {
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .userId(orderRequest.getUserId())
                .orderStatus(OrderStatus.PENDING)
                .build();
        purchaseOrderRepository.save(purchaseOrder);

        // 주문 진행 가능 여부
        boolean isOrderAvailable = true;
        List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
        for (PurchaseProductDto requestProduct : orderRequest.getPurchaseProducts()) {
            ProductInventory dbProductInventory =
                    productInventoryRepository.findByInventoryNo(requestProduct.getInventoryNo());
            if (dbProductInventory == null) {
                log.info("주문 상품 재고 확인 불가 ProductId {} InventoryNo {} ", requestProduct.getProductId(), requestProduct.getInventoryNo());
                isOrderAvailable = false;
                continue;
            }
            PurchaseOrderItem purchaseOrderItem = PurchaseOrderItem.builder()
                    .purchaseOrder(purchaseOrder)
                    .productInventory(dbProductInventory)
                    .orderQuantity(requestProduct.getQuantity())
                    .build();
            purchaseOrderItems.add(purchaseOrderItem);
        }

        if (!purchaseOrderItems.isEmpty()) {
            purchaseOrderItemRepository.saveAll(purchaseOrderItems);
        }

        return OrderProcess.builder()
                .purchaseOrder(purchaseOrder)
                .purchaseOrderItems(purchaseOrderItems)
                .isOrderAvailable(isOrderAvailable)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void purchaseOrder(OrderProcess orderProcess) throws RuntimeException {
        List<Long> inventoryNos = orderProcess.getPurchaseOrderItems().stream()
                .map(item -> item.getProductInventory().getInventoryNo())
                .toList();
        // 주문 상품 재고 조회
        List<ProductInventory> productInventories = productInventoryRepository.findByInventoryNoIn(inventoryNos);
        // 주문 상품 재고 차감
        for (PurchaseOrderItem purchaseOrderItem : orderProcess.getPurchaseOrderItems()) {
            ProductInventory productInventory = productInventories.stream()
                    .filter(inventory -> inventory.getInventoryNo().equals(purchaseOrderItem.getProductInventory().getInventoryNo()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("재고 정보 없음"));
            // 재고 부족한 경우 throw exception
            productInventory.decreaseQuantity(purchaseOrderItem.getOrderQuantity());

            // 주문 재고 이력 상태 변경
            purchaseOrderItem.completeOrder();
        }
        // 주문 완료 상태로 변경
        orderProcess.getPurchaseOrder().completed();

        purchaseOrderRepository.save(orderProcess.getPurchaseOrder());
        purchaseOrderItemRepository.saveAll(orderProcess.getPurchaseOrderItems());
    }

    @Transactional
    public void failPurchaseOrderProcess(OrderProcess orderProcess) {
        orderProcess.getPurchaseOrder().fail();
        for (PurchaseOrderItem purchaseOrderItem : orderProcess.getPurchaseOrderItems()) {
            purchaseOrderItem.fail();
        }
        purchaseOrderRepository.save(orderProcess.getPurchaseOrder());
        purchaseOrderItemRepository.saveAll(orderProcess.getPurchaseOrderItems());
    }
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderProcess getCancelOrderProcess(Long orderNo, String userId, HashMap<Long, CancelOrderItem> cancelOrderItems) {
        CancelOrderProcess cancelOrderProcess = CancelOrderProcess.builder().build();
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByOrderNoAndUserId(orderNo, userId);
        if (purchaseOrder == null) {
            throw new EntityNotFoundException("주문번호에 해당하는 주문이 존재하지 않습니다.");
        } else if (!OrderStatus.COMPLETED.equals(purchaseOrder.getOrderStatus())) {
            throw new RuntimeException("주문 완료 상태가 아닌 주문번호 입니다.");
        }
        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemRepository.findByPurchaseOrder(purchaseOrder);
        cancelOrderProcess.setPurchaseOrder(purchaseOrder);
        cancelOrderProcess.setPurchaseOrderItems(purchaseOrderItems);
        // 주문 상품 취소 진행
        cancelOrderItems(cancelOrderProcess, cancelOrderItems);
        cancelOrderProcess.success();
        return cancelOrderProcess;
    }


    private void cancelOrderItems(CancelOrderProcess cancelOrderProcess, HashMap<Long, CancelOrderItem> cancelOrderItems) {
        List<PurchaseOrderItemHistory> orderItemHistories = new ArrayList<>();

        // 전체 주문 상품 중에서 취소 요청 상품 찾기
        for (PurchaseOrderItem purchaseOrderItem : cancelOrderProcess.getPurchaseOrderItems()) {

            CancelOrderItem cancelOrderItem = cancelOrderItems.get(purchaseOrderItem.getOrderItemNo());
            if (cancelOrderItem != null) {
                // 주문 상품 취소 처리
                purchaseOrderItem.cancel(cancelOrderItem.getCancelQuantity());
                // 재고 복원
                ProductInventory productInventory = purchaseOrderItem.getProductInventory();
                productInventory.increaseQuantity(cancelOrderItem.getCancelQuantity());
                // 주문 상품 취소 이력 생성
                PurchaseOrderItemHistory orderItemHistory = PurchaseOrderItemHistory.builder()
                        .orderNo(purchaseOrderItem.getPurchaseOrder().getOrderNo())
                        .orderItemNo(purchaseOrderItem.getOrderItemNo())
                        .actionType(ActionType.CANCEL)
                        .changedQuantity(cancelOrderItem.getCancelQuantity())
                        .build();
                orderItemHistories.add(orderItemHistory);
            }
        }

        // 취소 정보 저장
        if (!orderItemHistories.isEmpty()) {
            // 주문 상품 취소 수량 저장
            purchaseOrderItemRepository.saveAll(cancelOrderProcess.getPurchaseOrderItems());
            // 주문 상품 취소 이력 저장
            purchaseOrderItemHistoryRepository.saveAll(orderItemHistories);
        } else {
            throw new RuntimeException("취소 요청한 상품이 주문에 존재하지 않습니다.");
        }
    }
}
