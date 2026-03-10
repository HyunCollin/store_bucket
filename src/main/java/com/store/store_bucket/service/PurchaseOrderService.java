package com.store.store_bucket.service;

import com.store.store_bucket.dto.OrderProcess;
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

    private final ProductInventoryRepository productInventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

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
                    .orElseThrow(() -> new RuntimeException("재고 정보 없음"));
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
}
