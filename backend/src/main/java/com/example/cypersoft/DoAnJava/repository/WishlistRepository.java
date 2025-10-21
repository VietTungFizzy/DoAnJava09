package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    
    // Find wishlist by user ID
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    // Find wishlist by user ID and name
    Optional<Wishlist> findByUserIdAndName(Integer userId, String name);
    
    // Check if wishlist exists for user with name
    boolean existsByUserIdAndName(Integer userId, String name);
    
    // Find default wishlist for user
    Optional<Wishlist> findByUserIdAndNameIgnoreCase(Integer userId, String name);
}
