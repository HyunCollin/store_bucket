package com.store.store_bucket.repository;

import com.store.store_bucket.entity.PurchaseOrderItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderItemHistoryRepository extends JpaRepository<PurchaseOrderItemHistory, Long> {
}
