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
    
    // Find wishlist item by wishlist and product
    Optional<WishlistItem> findByWishlistIdAndProductId(Integer wishlistId, Integer productId);
    
    // Find all items in a wishlist
    List<WishlistItem> findByWishlistIdOrderByCreatedAtDesc(Integer wishlistId);
    
    // Check if product exists in wishlist
    boolean existsByWishlistIdAndProductId(Integer wishlistId, Integer productId);
    
    // Delete wishlist item by wishlist and product
    void deleteByWishlistIdAndProductId(Integer wishlistId, Integer productId);
    
    // Count items in wishlist
    long countByWishlistId(Integer wishlistId);
    
    // Find items by product ID (for analytics)
    List<WishlistItem> findByProductId(Integer productId);
    
    // Custom query to get wishlist items with product details
    @Query("SELECT wi FROM WishlistItem wi " +
           "JOIN FETCH wi.product p " +
           "WHERE wi.wishlist.id = :wishlistId " +
           "ORDER BY wi.createdAt DESC")
    List<WishlistItem> findWishlistItemsWithProduct(@Param("wishlistId") Integer wishlistId);
}

