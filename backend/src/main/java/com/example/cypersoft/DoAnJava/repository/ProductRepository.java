package com.example.cypersoft.DoAnJava.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.Sku;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByStatus(String status, Pageable pageable);
    
    // Filter by name (keyword search)
    Page<Product> findByNameContainingIgnoreCaseAndStatus(String keyword, String status, Pageable pageable);
    
    // Filter by brand
    Page<Product> findByBrandNameIgnoreCase(String brandName, Pageable pageable);
    
    // Filter by brand and status
    Page<Product> findByBrandNameIgnoreCaseAndStatus(String brandName, String status, Pageable pageable);
    
    // Filter by categories
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds")
    Page<Product> findByCategoryIds(@Param("categoryIds") List<Integer> categoryIds, Pageable pageable);
    
    // Filter by categories and status
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds AND p.status = :status")
    Page<Product> findByCategoryIdsAndStatus(@Param("categoryIds") List<Integer> categoryIds, @Param("status") String status, Pageable pageable);
    
    // Query with all filters including price - use subquery for price filtering
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN p.brand b " +
           "LEFT JOIN p.categories c " +
           "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:brandName IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :brandName, '%'))) " +
           "AND (:minPrice IS NULL OR p.id IN (SELECT s.product.id FROM Sku s WHERE s.price >= :minPrice AND s.status = 'active')) " +
           "AND (:maxPrice IS NULL OR p.id IN (SELECT s.product.id FROM Sku s WHERE s.price <= :maxPrice AND s.status = 'active')) " +
           "AND (:categoryIds IS NULL OR c.id IN :categoryIds) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND p.deletedAt IS NULL")
    Page<Product> findProductsWithFilters(
        @Param("name") String name,
        @Param("keyword") String keyword,
        @Param("brandName") String brandName,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("categoryIds") List<Integer> categoryIds,
        @Param("status") String status,
        Pageable pageable
    );
    
    // Load SKUs for products
    @Query("SELECT s FROM Sku s WHERE s.product.id IN :productIds AND s.status = 'active'")
    List<Sku> findActiveSkusByProductIds(@Param("productIds") List<Integer> productIds);
    
    // Get all products without pagination (for /all endpoint) - use subquery for price filtering
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN p.brand b " +
           "LEFT JOIN p.categories c " +
           "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:brandName IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :brandName, '%'))) " +
           "AND (:minPrice IS NULL OR p.id IN (SELECT s.product.id FROM Sku s WHERE s.price >= :minPrice AND s.status = 'active')) " +
           "AND (:maxPrice IS NULL OR p.id IN (SELECT s.product.id FROM Sku s WHERE s.price <= :maxPrice AND s.status = 'active')) " +
           "AND (:categoryIds IS NULL OR c.id IN :categoryIds) " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND p.deletedAt IS NULL")
    List<Product> findAllProductsWithFilters(
        @Param("name") String name,
        @Param("keyword") String keyword,
        @Param("brandName") String brandName,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("categoryIds") List<Integer> categoryIds,
        @Param("status") String status,
        Sort sort
    );
} 