package com.store.store_bucket.repository;

import com.store.store_bucket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p JOIN FETCH p.inventories")
    List<Product> findAllWithInventories();

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN FETCH p.inventories " +
            "WHERE p.productId IN :productIds")
    List<Product> findByProductIdIn(@Param("productIds") List<String> productIds);
}
