package com.store.store_bucket.service;

import com.store.store_bucket.dto.*;
import com.store.store_bucket.entity.*;
import com.store.store_bucket.enums.ActionType;
import com.store.store_bucket.enums.OrderStatus;
import com.store.store_bucket.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final ProductInventoryRepository productInventoryRepository;
    private final ProductInventoryHistoryRepository productInventoryHistoryRepository;
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
        List<ProductInventoryHistory> inventoryHistories = new ArrayList<>();
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

            // 상품의 마지막 유효 주문 정보 저장
            ProductInventoryHistory productInventoryHistory = ProductInventoryHistory.builder()
                    .inventoryNo(purchaseOrderItem.getProductInventory().getInventoryNo())
                    .actionType(ActionType.ORDER)
                    .lastOrderNo(purchaseOrderItem.getPurchaseOrder().getOrderNo())
                    .lastOrderTime(LocalDateTime.now())
                    .build();
            inventoryHistories.add(productInventoryHistory);
        }
        // 주문 완료 상태로 변경
        orderProcess.getPurchaseOrder().completed();

        purchaseOrderRepository.save(orderProcess.getPurchaseOrder());
        purchaseOrderItemRepository.saveAll(orderProcess.getPurchaseOrderItems());
        productInventoryHistoryRepository.saveAll(inventoryHistories);
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

    @Transactional(readOnly = true)
    public ViewOrderPage getOrderListByUserId(String userId, int viewPageNo, int viewPageCount) {
        // 1. page 처리
        ViewOrderPage viewOrderPage = ViewOrderPage.builder()
                .pageNo(viewPageNo)
                .pageCount(viewPageCount)
                .build();
        Pageable pageable = createBasePageable(viewPageNo, viewPageCount);
        // 2. 주문 조회
        Page<PurchaseOrder> page = purchaseOrderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.COMPLETED, pageable);
        List<PurchaseOrder> purchaseOrders = page.stream().toList();
        viewOrderPage.setTotalPages(page.getTotalPages());
        viewOrderPage.setHasNextPage(page.hasNext());
        viewOrderPage.setHasPreviousPage(page.hasPrevious());

        // 3 주문 상품 조회 및 dto 생성
        List<ViewOrder> viewOrders = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            ViewOrder viewOrder = ViewOrder.builder()
                    .orderNo(purchaseOrder.getOrderNo())
                    .userId(purchaseOrder.getUserId())
                    .orderStatus(purchaseOrder.getOrderStatus().toString())
                    .createdAt(purchaseOrder.getCreatedAtString())
                    .build();
            List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemRepository.findByPurchaseOrder(purchaseOrder);
            List<ViewOrderItem> viewOrderItems = new ArrayList<>();
            for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
                ViewOrderItem viewOrderItem = ViewOrderItem.builder()
                        .orderItemNo(purchaseOrderItem.getOrderItemNo())
                        .orderNo(purchaseOrderItem.getPurchaseOrder().getOrderNo())
                        .productId(purchaseOrderItem.getProductInventory().getProduct().getProductId())
                        .inventoryNo(purchaseOrderItem.getProductInventory().getInventoryNo())
                        .color(purchaseOrderItem.getProductInventory().getColor())
                        .size(purchaseOrderItem.getProductInventory().getSize())
                        .orderQuantity(purchaseOrderItem.getOrderQuantity())
                        .cancelQuantity(purchaseOrderItem.getCancelQuantity())
                        .itemStatus(purchaseOrderItem.getItemStatus())
                        .createdAt(purchaseOrderItem.getCreatedAt())
                        .cancelAt(purchaseOrderItem.getCancelAt())
                        .build();
                viewOrderItems.add(viewOrderItem);
            }
            viewOrder.setViewOrderItems(viewOrderItems);
            viewOrders.add(viewOrder);
        }
        viewOrderPage.setViewOrders(viewOrders);
        return viewOrderPage;
    }


    private void cancelOrderItems(CancelOrderProcess cancelOrderProcess, HashMap<Long, CancelOrderItem> cancelOrderItems) {
        List<PurchaseOrderItemHistory> orderItemHistories = new ArrayList<>();
        List<ProductInventoryHistory> inventoryHistories = new ArrayList<>();
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
                // 상품의 마지막 유효 주문 정보 저장
                ProductInventoryHistory productInventoryHistory = ProductInventoryHistory.builder()
                        .inventoryNo(purchaseOrderItem.getProductInventory().getInventoryNo())
                        .actionType(ActionType.CANCEL)
                        .lastOrderNo(purchaseOrderItem.getPurchaseOrder().getOrderNo())
                        .lastOrderTime(LocalDateTime.now())
                        .build();
                inventoryHistories.add(productInventoryHistory);
            }
        }

        // 취소 정보 저장
        if (!orderItemHistories.isEmpty()) {
            // 주문 상품 취소 수량 저장
            purchaseOrderItemRepository.saveAll(cancelOrderProcess.getPurchaseOrderItems());
            // 주문 상품 취소 이력 저장
            purchaseOrderItemHistoryRepository.saveAll(orderItemHistories);
            // 상품 마지막 유효 주문 정보 저장
            productInventoryHistoryRepository.saveAll(inventoryHistories);
        } else {
            throw new RuntimeException("취소 요청한 상품이 주문에 존재하지 않습니다.");
        }
    }

    /**
     * 화면 pageNo와 db pageIndex 1 차이 보정 및 기본 정렬 설정
     */
    private Pageable createBasePageable(int viewPageNo, int viewPageCount) {
        int pageIndex = Math.max((viewPageNo - 1), 0);
        return PageRequest.of(pageIndex, viewPageCount, Sort.by("createdAt").descending());
    }

}
