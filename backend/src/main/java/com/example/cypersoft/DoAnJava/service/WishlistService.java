package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.WishlistRequest;
import com.example.cypersoft.DoAnJava.dto.WishlistResponse;
import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.Sku;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.entity.Wishlist;
import com.example.cypersoft.DoAnJava.entity.WishlistItem;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;
import com.example.cypersoft.DoAnJava.repository.SkuRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {
    
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    
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
        
        // Get SKU (can be provided directly or derived from product)
        Sku sku;
        if (request.getSkuId() != null) {
            // Direct SKU reference
            sku = skuRepository.findById(request.getSkuId())
                    .orElseThrow(() -> new RuntimeException("SKU not found"));
        } else if (request.getProductId() != null) {
            // Find SKU from product (use first SKU as default)
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            // Query SKUs by product_id and use the first one
            List<Sku> skus = skuRepository.findByProductId(request.getProductId());
            if (skus.isEmpty()) {
                throw new RuntimeException("SKU not found for this product");
            }
            sku = skus.get(0); // Use first SKU as default
        } else {
            throw new RuntimeException("Either productId or skuId must be provided");
        }
        
        // Check if SKU is already in wishlist
        if (wishlistItemRepository.existsByWishlistIdAndSkuId(wishlist.getId(), sku.getId())) {
            throw new RuntimeException("Item already in wishlist");
        }
        
        // Create wishlist item
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setWishlist(wishlist);
        wishlistItem.setSku(sku);
        
        WishlistItem savedItem = wishlistItemRepository.save(wishlistItem);
        return convertToResponse(savedItem);
    }
    
    // Remove item from wishlist (by productId - will find and remove SKU)
    @Transactional
    public void removeFromWishlist(Integer productId) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        // For backwards compatibility: treat productId as skuId
        Integer skuId = productId;
        
        if (!wishlistItemRepository.existsByWishlistIdAndSkuId(wishlist.getId(), skuId)) {
            throw new RuntimeException("Item not found in wishlist");
        }
        
        wishlistItemRepository.deleteByWishlistIdAndSkuId(wishlist.getId(), skuId);
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
    
    // Check if product is in wishlist (productId treated as skuId)
    public boolean isProductInWishlist(Integer productId) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        // Treat productId as skuId for backwards compatibility
        return wishlistItemRepository.existsByWishlistIdAndSkuId(wishlist.getId(), productId);
    }
    
    // Update wishlist item
    @Transactional
    public WishlistResponse updateWishlistItem(Integer productId, WishlistRequest request) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        // Treat productId as skuId for backwards compatibility
        WishlistItem wishlistItem = wishlistItemRepository.findByWishlistIdAndSkuId(wishlist.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item not found in wishlist"));
        
        // Nothing to update in simple wishlist (just sku_id reference)
        return convertToResponse(wishlistItem);
    }
    
    // Search wishlist items
    public Page<WishlistResponse> searchWishlist(String keyword, int page, int size) {
        User currentUser = getCurrentUser();
        Wishlist wishlist = getOrCreateDefaultWishlist(currentUser);
        
        List<WishlistItem> items = wishlistItemRepository.findWishlistItemsWithProduct(wishlist.getId());
        List<WishlistResponse> responses = items.stream()
                .filter(item -> item.getSku() != null && 
                               item.getSku().getProductName() != null &&
                               item.getSku().getProductName().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, responses.size());
        List<WishlistResponse> pageContent = start < responses.size() ? 
                responses.subList(start, end) : List.of();
        
        return new PageImpl<>(pageContent, PageRequest.of(page, size), responses.size());
    }
    
    // Note: Priority, notifications, and price tracking features removed in simplified wishlist
    
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
        Sku sku = wishlistItem.getSku();
        Product product = sku != null ? sku.getProduct() : null;
        
        response.setId(wishlistItem.getId());
        response.setUserId(wishlistItem.getWishlist().getUser().getId());
        response.setProductId(sku != null ? sku.getId() : null); // Return SKU ID as productId for compatibility
        response.setProductName(sku != null ? sku.getProductName() : null);
        response.setProductDescription(product != null ? product.getDescription() : null);
        response.setProductPrice(sku != null ? sku.getPrice() : null);
        response.setProductImageUrl(sku != null ? sku.getImageUrl() : null);
        response.setProductStatus(sku != null ? sku.getStatus() : null);
        response.setCreatedAt(wishlistItem.getCreatedAt());
        response.setProductCreatedAt(product != null ? product.getCreatedAt() : null);
        response.setProductUpdatedAt(product != null ? product.getUpdatedAt() : null);
        
        return response;
    }
}
