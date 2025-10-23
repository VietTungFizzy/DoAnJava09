package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Integer> {
    
    Optional<Sku> findBySkuCode(String skuCode);
    
    boolean existsBySkuCode(String skuCode);
}

