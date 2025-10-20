package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.WishlistRequest;
import com.example.cypersoft.DoAnJava.dto.WishlistResponse;
import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.entity.Wishlist;
import com.example.cypersoft.DoAnJava.entity.WishlistItem;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
import com.example.cypersoft.DoAnJava.repository.WishlistRepository;
import com.example.cypersoft.DoAnJava.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    private static final String DEFAULT_WISHLIST_NAME = "Default";
    
    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // Get or create default wishlist for user
    private Wishlist getOrCreateDefaultWishlist(User user) {
        return wishlistRepository.findByUserIdAndName(user.getId(), DEFAULT_WISHLIST_NAME)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUser(user);
                    newWishlist.setName(DEFAULT_WISHLIST_NAME);
                    return wishlistRepository.save(newWishlist);
                });
    }
    
    // Add item to wishlist
    @Transactional
    public WishlistResponse addToWishlist(WishlistRequest request) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if product is already in wishlist
        if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), request.getProductId())) {
            throw new RuntimeException("Product already in wishlist");
        }
        
        // Create wishlist item
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setProduct(product);
        wishlistItem.setNotes(request.getNotes());
        wishlistItem.setPriority(request.getPriority());
        wishlistItem.setIsNotified(request.getIsNotified());
        
        WishlistItem savedItem = wishlistItemRepository.save(wishlistItem);
        return convertToResponse(savedItem);
    }
    
    // Remove item from wishlist
    @Transactional
    public void removeFromWishlist(Integer productId) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            throw new RuntimeException("Product not found in wishlist");
        }
        
        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
    }
    
    // Get user's wishlist with pagination
    public Page<WishlistResponse> getUserWishlist(int page, int size, String sortBy, String sortDir) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        List<WishlistItem> items = wishlistItemRepository.findWishlistItemsWithProduct(wishlist.getId());
        List<WishlistResponse> responses = items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, responses.size());
        List<WishlistResponse> pageContent = start < responses.size() ? 
                responses.subList(start, end) : List.of();
        
        return new PageImpl<>(pageContent, PageRequest.of(page, size), responses.size());
    }
    
    // Get user's wishlist count
    public long getWishlistCount() {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        return wishlistItemRepository.countByWishlistId(wishlist.getId());
    }
    
    // Check if product is in wishlist
    public boolean isProductInWishlist(Integer productId) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        return wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
    }
    
    // Update wishlist item
    @Transactional
    public WishlistResponse updateWishlistItem(Integer productId, WishlistRequest request) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        WishlistItem wishlistItem = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not found in wishlist"));
        
        wishlistItem.setNotes(request.getNotes());
        wishlistItem.setPriority(request.getPriority());
        wishlistItem.setIsNotified(request.getIsNotified());
        
        WishlistItem updatedItem = wishlistItemRepository.save(wishlistItem);
        return convertToResponse(updatedItem);
    }
    
    // Search wishlist items
    public Page<WishlistResponse> searchWishlist(String keyword, int page, int size) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        List<WishlistItem> items = wishlistItemRepository.findWishlistItemsWithProduct(wishlist.getId());
        List<WishlistResponse> responses = items.stream()
                .filter(item -> item.getProduct().getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, responses.size());
        List<WishlistResponse> pageContent = start < responses.size() ? 
                responses.subList(start, end) : List.of();
        
        return new PageImpl<>(pageContent, PageRequest.of(page, size), responses.size());
    }
    
    // Get wishlist items by priority
    public List<WishlistResponse> getWishlistByPriority(Integer priority) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        List<WishlistItem> items = wishlistItemRepository.findWishlistItemsWithProduct(wishlist.getId());
        return items.stream()
                .filter(item -> item.getPriority().equals(priority))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get wishlist items with notifications enabled
    public List<WishlistResponse> getWishlistWithNotifications() {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        List<WishlistItem> items = wishlistItemRepository.findWishlistItemsWithProduct(wishlist.getId());
        return items.stream()
                .filter(item -> item.getIsNotified())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Clear entire wishlist
    @Transactional
    public void clearWishlist() {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        List<WishlistItem> items = wishlistItemRepository.findByWishlistIdOrderByCreatedAtDesc(wishlist.getId());
        wishlistItemRepository.deleteAll(items);
    }
    
    // Convert entity to response DTO
    private WishlistResponse convertToResponse(WishlistItem wishlistItem) {
        WishlistResponse response = new WishlistResponse();
        response.setId(wishlistItem.getId());
        response.setUserId(wishlistItem.getWishlist().getUser().getId());
        response.setProductId(wishlistItem.getProduct().getId());
        response.setProductName(wishlistItem.getProduct().getName());
        response.setProductDescription(wishlistItem.getProduct().getDescription());
        response.setProductPrice(wishlistItem.getProduct().getPrice());
        response.setProductImageUrl(wishlistItem.getProduct().getImageUrl());
        response.setProductStatus(wishlistItem.getProduct().getStatus());
        response.setNotes(wishlistItem.getNotes());
        response.setPriority(wishlistItem.getPriority());
        response.setIsNotified(wishlistItem.getIsNotified());
        response.setCreatedAt(wishlistItem.getCreatedAt());
        response.setProductCreatedAt(wishlistItem.getProduct().getCreatedAt());
        response.setProductUpdatedAt(wishlistItem.getProduct().getUpdatedAt());
        
        return response;
    }
}
