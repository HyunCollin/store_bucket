package com.store.store_bucket;

import com.store.store_bucket.dto.*;
import com.store.store_bucket.entity.Product;
import com.store.store_bucket.entity.ProductInventory;
import com.store.store_bucket.entity.PurchaseOrderItem;
import com.store.store_bucket.enums.OrderStatus;
import com.store.store_bucket.service.ProductService;
import com.store.store_bucket.service.PurchaseOrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class StoreBucketApplicationTests {
    //    @MockBean
//    private TokenService tokenService;
    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Test
    public void getAllProductsTest() {
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            System.out.println(product.toString());
            List<ProductInventory> inventories = product.getInventories();
            for (ProductInventory productInventory : inventories) {
                System.out.println(String.format("%s %s %s", productInventory.getColor(), productInventory.getSize(), productInventory.getQuantity()));
            }
        }
    }

    @Test
    public void createOrderTest() {
        String userId = "user_test1";
        PurchaseProductDto item1 = PurchaseProductDto.builder()
                .inventoryNo(3L)
                .productId("11101JS505")
                .color("BK")
                .size("100")
                .quantity(3)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(6L)
                .productId("11101JS505")
                .color("WH")
                .size("105")
                .quantity(1)
                .build();
        OrderProcess orderProcess = purchaseOrder(userId, item1, item2);
    }

    @Test
    public void createOrder2Test() {
        String userId = "hyun_test1";
        PurchaseProductDto item1 = PurchaseProductDto.builder()
                .inventoryNo(9L)
                .productId("82193SRK52")
                .color("GY")
                .size("2")
                .quantity(10)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(15L)
                .productId("M31E5AC014")
                .color("OR")
                .size("FREE")
                .quantity(5)
                .build();

        OrderProcess orderProcess = purchaseOrder(userId, item1, item2);
    }

    private OrderProcess purchaseOrder(String userId, PurchaseProductDto item1, PurchaseProductDto item2) {

        // 주문 객체 정보 생성
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);
        orderRequest.setOrderStatus(OrderStatus.PENDING);
        // 구매 상품


        List<PurchaseProductDto> purchaseProducts = new ArrayList<>();
        purchaseProducts.add(item1);
        purchaseProducts.add(item2);

        orderRequest.setPurchaseProducts(purchaseProducts);

        // 주문 생성
        OrderProcess orderProcess = purchaseOrderService.saveTempOrder(orderRequest);
        if (orderProcess.isOrderAvailable()) {
            try {
                purchaseOrderService.purchaseOrder(orderProcess);
            } catch (Exception e) {
                // 주문 처리 중 예외 발생 시 주문 실패 처리
                purchaseOrderService.failPurchaseOrderProcess(orderProcess);
            }
        } else {
            // 주문 요청 상품 검증 실패
            purchaseOrderService.failPurchaseOrderProcess(orderProcess);
        }
        return orderProcess;
    }

    @Test
    public void createOrderFailTest() {
        String userId = "test1";
        // 주문 객체 정보 생성
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);
        orderRequest.setOrderStatus(OrderStatus.PENDING);
        // 구매 상품
        PurchaseProductDto item1 = PurchaseProductDto.builder()
                .inventoryNo(3L)
                .productId("11101JS505")
                .color("BK")
                .size("100")
                .quantity(999)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(6L)
                .productId("11101JS505")
                .color("WH")
                .size("105")
                .quantity(999)
                .build();

        List<PurchaseProductDto> purchaseProducts = new ArrayList<>();
        purchaseProducts.add(item1);
        purchaseProducts.add(item2);

        orderRequest.setPurchaseProducts(purchaseProducts);

        // 주문 생성
        OrderProcess orderProcess = purchaseOrderService.saveTempOrder(orderRequest);
        if (orderProcess.isOrderAvailable()) {
            try {
                purchaseOrderService.purchaseOrder(orderProcess);
            } catch (Exception e) {
                // 주문 처리 중 예외 발생 시 주문 실패 처리
                purchaseOrderService.failPurchaseOrderProcess(orderProcess);
            }
        } else {
            // 주문 요청 상품 검증 실패
            purchaseOrderService.failPurchaseOrderProcess(orderProcess);
        }
    }

    @Test
    public void createOrderNotExistInventoryNoTest() {
        String userId = "test1";
        // 주문 객체 정보 생성
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(userId);
        orderRequest.setOrderStatus(OrderStatus.PENDING);
        // 구매 상품
        PurchaseProductDto item1 = PurchaseProductDto.builder()
                .inventoryNo(9999L)
                .productId("11101JS505")
                .color("BK")
                .size("100")
                .quantity(999)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(99999L)
                .productId("11101JS505")
                .color("WH")
                .size("105")
                .quantity(999)
                .build();

        List<PurchaseProductDto> purchaseProducts = new ArrayList<>();
        purchaseProducts.add(item1);
        purchaseProducts.add(item2);

        orderRequest.setPurchaseProducts(purchaseProducts);

        // 주문 생성
        try {
            OrderProcess orderProcess = purchaseOrderService.saveTempOrder(orderRequest);
            if (orderProcess.isOrderAvailable()) {
                try {
                    purchaseOrderService.purchaseOrder(orderProcess);
                } catch (Exception e) {
                    // 주문 처리 중 예외 발생 시 주문 실패 처리
                    purchaseOrderService.failPurchaseOrderProcess(orderProcess);
                }
            } else {
                // 주문 요청 상품 검증 실패
                purchaseOrderService.failPurchaseOrderProcess(orderProcess);
            }
        } catch (Exception e) {
            System.out.println("상품 정보가 없는 주문 요청 입니다." + e.getMessage());
        }
    }

    @Test
    public void cancelOrderTest() {
        // 주문 부분 취소 테스트
        // 신규 주문 생성
        String userId = "cancel_test";
        PurchaseProductDto item1 = PurchaseProductDto.builder()
                .inventoryNo(2L)
                .productId("11101JS505")
                .color("WH")
                .size("95")
                .quantity(3)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(16L)
                .productId("M31E5AC014")
                .color("IV")
                .size("FREE")
                .quantity(1)
                .build();

        OrderProcess orderProcess = purchaseOrder(userId, item1, item2);
        // 주문 취소 요청 정보 생성
        Long cancelOrderNo = orderProcess.getPurchaseOrder().getOrderNo();
        if (orderProcess.getPurchaseOrderItems().isEmpty()) {
            Assertions.fail("주문 상품이 존재하지 않습니다.");
            return;
        }
        PurchaseOrderItem purchaseOrderItem = orderProcess.getPurchaseOrderItems()
                .stream().findFirst().orElseThrow(() -> new RuntimeException("주문 상품이 존재하지 않습니다."));

        HashMap<Long, CancelOrderItem> cancelOrderItems = new HashMap<>();
        CancelOrderItem cancelOrderItem = CancelOrderItem.builder()
                .orderItemNo(purchaseOrderItem.getOrderItemNo())
                // 실제 주문 수량에서 1개 취소 요청
                .cancelQuantity(1)
                .build();
        cancelOrderItems.put(purchaseOrderItem.getOrderItemNo(), cancelOrderItem);
        // 주문 취소 진행
        purchaseOrderService.getCancelOrderProcess(cancelOrderNo, orderProcess.getPurchaseOrder().getUserId(), cancelOrderItems);
    }

    @Test
    public void cancelOrderFailTest() {
        // 주문 부분 취소 테스트
        // 신규 주문 생성
        String userId = "cancel_test";
        PurchaseProductDto item1 = PurchaseProductDto.builder()
                .inventoryNo(2L)
                .productId("11101JS505")
                .color("WH")
                .size("95")
                .quantity(3)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(16L)
                .productId("M31E5AC014")
                .color("IV")
                .size("FREE")
                .quantity(1)
                .build();
        OrderProcess orderProcess = purchaseOrder(userId, item1, item2);
        // 주문 취소 요청 정보 생성
        Long cancelOrderNo = orderProcess.getPurchaseOrder().getOrderNo();
        if (orderProcess.getPurchaseOrderItems().isEmpty()) {
            Assertions.fail("주문 상품이 존재하지 않습니다.");
            return;
        }
        PurchaseOrderItem purchaseOrderItem = orderProcess.getPurchaseOrderItems()
                .stream().findFirst().orElseThrow(() -> new RuntimeException("주문 상품이 존재하지 않습니다."));

        HashMap<Long, CancelOrderItem> cancelOrderItems = new HashMap<>();
        CancelOrderItem cancelOrderItem = CancelOrderItem.builder()
                .orderItemNo(purchaseOrderItem.getOrderItemNo())
                // 실제 주문 수량에서 100개 취소 요청
                .cancelQuantity(100)
                .build();
        cancelOrderItems.put(purchaseOrderItem.getOrderItemNo(), cancelOrderItem);
        // 주문 취소 진행
        try {
            purchaseOrderService.getCancelOrderProcess(cancelOrderNo, orderProcess.getPurchaseOrder().getUserId(), cancelOrderItems);
        } catch (IllegalArgumentException e) {
            // 취소 수량이 실제 주문 수량보다 많은 경우 예외 발생
            System.out.println("취소 수량이 실제 주문 수량보다 많습니다. 예외 발생: " + e.getMessage());
        }
    }


    @Test
    public void getViewOrderPageTest() {
        int pageNum = 1;
        int pageCount = 5;
        String userId = "test1";

        ViewOrderPage viewOrderPage = purchaseOrderService.getOrderListByUserId(userId, pageNum, pageCount);
        List<ViewOrder> viewOrders = viewOrderPage.getViewOrders();
        for (ViewOrder viewOrder : viewOrders) {
            // 주문 정보 출력
            System.out.println(String.format("주문번호: %d, 주문상태: %s, 주문일시: %s",
                    viewOrder.getOrderNo(), viewOrder.getOrderStatus(), viewOrder.getCreatedAt()));
            // 주문 상품 정보 출력
            for (ViewOrderItem viewOrderItem : viewOrder.getViewOrderItems()) {
                System.out.println(String.format("  주문상품번호: %d, 상품 ID: %s, 색상: %s, 사이즈: %s, 주문수량: %d, 취소수량: %d, 상품상태: %s",
                        viewOrderItem.getOrderItemNo(), viewOrderItem.getProductId(), viewOrderItem.getColor(),
                        viewOrderItem.getSize(), viewOrderItem.getOrderQuantity(), viewOrderItem.getCancelQuantity(),
                        viewOrderItem.getItemStatus()));
            }

        }

    }
}
