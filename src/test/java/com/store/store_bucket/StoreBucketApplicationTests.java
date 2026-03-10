package com.store.store_bucket;

import com.store.store_bucket.dto.OrderProcess;
import com.store.store_bucket.dto.OrderRequest;
import com.store.store_bucket.dto.PurchaseProductDto;
import com.store.store_bucket.entity.Product;
import com.store.store_bucket.entity.ProductInventory;
import com.store.store_bucket.enums.OrderStatus;
import com.store.store_bucket.service.ProductService;
import com.store.store_bucket.service.PurchaseOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class StoreBucketApplicationTests {
    @Autowired
    private ProductService productService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Test
    public void getAllProductsTest(){
        List<Product> products = productService.getAllProducts();
        for (Product product : products){
            System.out.println(product.toString());
            List<ProductInventory> inventories = product.getInventories();
            for (ProductInventory productInventory : inventories){
                System.out.println(String.format("%s %s %s", productInventory.getColor(), productInventory.getSize(), productInventory.getQuantity()));
            }
        }
    }

    @Test
    public void createOrderTest(){
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
                .quantity(3)
                .build();

        PurchaseProductDto item2 = PurchaseProductDto.builder()
                .inventoryNo(6L)
                .productId("11101JS505")
                .color("WH")
                .size("105")
                .quantity(1)
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
    public void createOrderFailTest(){
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
    public void createOrderNotExistInventoryNoTest(){
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
}
