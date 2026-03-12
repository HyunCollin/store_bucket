package com.store.store_bucket.repository;

import com.store.store_bucket.entity.PurchaseOrder;
import com.store.store_bucket.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    PurchaseOrder findByOrderNoAndUserId(Long orderNo, String userId);

    Page<PurchaseOrder> findByUserIdAndOrderStatus(String userId, OrderStatus orderStatus, Pageable pageable);
}
