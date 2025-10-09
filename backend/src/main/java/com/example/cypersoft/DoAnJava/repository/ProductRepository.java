package com.example.cypersoft.DoAnJava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cypersoft.DoAnJava.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByStatus(String status, Pageable pageable);
} 