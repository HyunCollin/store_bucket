package com.store.store_bucket.repository;

import com.store.store_bucket.entity.ProductInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductInventory findByProductProductIdAndColorAndSizeAndQuantityGreaterThanEqual(String productId, String color, String size, Integer quantity);
}
