package com.store.store_bucket.repository;

import com.store.store_bucket.entity.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductInventory findByProductProductIdAndColorAndSizeAndQuantityGreaterThanEqual(String productId, String color, String size, Integer quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductInventory findByProductProductIdAndColorAndSize(String productId, String color, String size);

    ProductInventory findByInventoryNo(Long inventoryNo);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ProductInventory> findByInventoryNoIn(List<Long> inventoryNos);
}
