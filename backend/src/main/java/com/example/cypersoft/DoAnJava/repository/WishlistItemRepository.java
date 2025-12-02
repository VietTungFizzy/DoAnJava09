package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {
    
    // Find wishlist item by wishlist and SKU
    Optional<WishlistItem> findByWishlistIdAndSkuId(Integer wishlistId, Integer skuId);
    
    // Find all items in a wishlist
    List<WishlistItem> findByWishlistIdOrderByCreatedAtDesc(Integer wishlistId);
    
    // Check if SKU exists in wishlist
    boolean existsByWishlistIdAndSkuId(Integer wishlistId, Integer skuId);
    
    // Delete wishlist item by wishlist and SKU
    void deleteByWishlistIdAndSkuId(Integer wishlistId, Integer skuId);
    
    // Count items in wishlist
    long countByWishlistId(Integer wishlistId);
    
    // Find items by SKU ID (for analytics)
    List<WishlistItem> findBySkuId(Integer skuId);
    
    // Custom query to get wishlist items with SKU and product details
    @Query("SELECT wi FROM WishlistItem wi " +
           "JOIN FETCH wi.sku s " +
           "JOIN FETCH s.product p " +
           "WHERE wi.wishlist.id = :wishlistId " +
           "ORDER BY wi.createdAt DESC")
    List<WishlistItem> findWishlistItemsWithProduct(@Param("wishlistId") Integer wishlistId);
    
    // Find items by wishlist and priority
    @Query("SELECT wi FROM WishlistItem wi " +
           "JOIN FETCH wi.sku s " +
           "JOIN FETCH s.product p " +
           "WHERE wi.wishlist.id = :wishlistId AND wi.priority = :priority " +
           "ORDER BY wi.createdAt DESC")
    List<WishlistItem> findByWishlistIdAndPriority(@Param("wishlistId") Integer wishlistId, @Param("priority") Integer priority);
    
    // Find items by wishlist with notifications enabled
    @Query("SELECT wi FROM WishlistItem wi " +
           "JOIN FETCH wi.sku s " +
           "JOIN FETCH s.product p " +
           "WHERE wi.wishlist.id = :wishlistId AND wi.isNotified = true " +
           "ORDER BY wi.createdAt DESC")
    List<WishlistItem> findByWishlistIdAndIsNotifiedTrue(@Param("wishlistId") Integer wishlistId);
}

