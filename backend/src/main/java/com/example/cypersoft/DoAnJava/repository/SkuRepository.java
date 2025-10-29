package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Integer> {
    
    Optional<Sku> findBySkuCode(String skuCode);
    
    boolean existsBySkuCode(String skuCode);
    
    @Query("SELECT s FROM Sku s WHERE s.product.id = :productId")
    List<Sku> findByProductId(@Param("productId") Integer productId);
}

