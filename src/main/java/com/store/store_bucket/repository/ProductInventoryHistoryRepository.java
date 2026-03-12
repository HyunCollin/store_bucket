package com.store.store_bucket.repository;

import com.store.store_bucket.entity.ProductInventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryHistoryRepository extends JpaRepository<ProductInventoryHistory, Long>, ProductInventoryHistoryRepositoryCustom {
}
