package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.WishlistRequest;
import com.example.cypersoft.DoAnJava.dto.WishlistResponse;
import com.example.cypersoft.DoAnJava.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    // Add item to wishlist
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addToWishlist(@Valid @RequestBody WishlistRequest request) {
        try {
            WishlistResponse response = wishlistService.addToWishlist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Remove item from wishlist
    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Integer productId) {
        try {
            wishlistService.removeFromWishlist(productId);
            return ResponseEntity.ok(createSuccessResponse("Product removed from wishlist"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Get user's wishlist with pagination
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            return ResponseEntity.ok(wishlistService.getUserWishlist(page, size, sortBy, sortDir));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Get wishlist count
    @GetMapping("/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getWishlistCount() {
        try {
            long count = wishlistService.getWishlistCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Check if product is in wishlist
    @GetMapping("/check/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> isProductInWishlist(@PathVariable Integer productId) {
        try {
            boolean isInWishlist = wishlistService.isProductInWishlist(productId);
            return ResponseEntity.ok(Map.of("isInWishlist", isInWishlist));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Update wishlist item
    @PutMapping("/product/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateWishlistItem(@PathVariable Integer productId, 
                                             @Valid @RequestBody WishlistRequest request) {
        try {
            WishlistResponse response = wishlistService.updateWishlistItem(productId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Search wishlist items
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> searchWishlist(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(wishlistService.searchWishlist(keyword, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Get wishlist items by priority
    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getWishlistByPriority(@PathVariable Integer priority) {
        try {
            List<WishlistResponse> response = wishlistService.getWhistlistByPriority(priority);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Get wishlist items with notifications enabled
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getWishlistWithNotifications() {
        try {
            List<WishlistResponse> response = wishlistService.getWishlistWithNotifications();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Clear entire wishlist
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> clearWishlist() {
        try {
            wishlistService.clearWishlist();
            return ResponseEntity.ok(createSuccessResponse("Wishlist cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Toggle product in wishlist (add if not exists, remove if exists)
    @PostMapping("/toggle/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> toggleWishlistItem(@PathVariable Integer productId) {
        try {
            // Resolve Product id (or SKU id) to an actual SKU id
            Integer skuId = wishlistService.resolveSkuIdFromProductId(productId);

            boolean isInWishlist = wishlistService.isProductInWishlist(skuId);

            if (isInWishlist) {
                wishlistService.removeFromWishlist(skuId);
                return ResponseEntity.ok(createSuccessResponse("Product removed from wishlist"));
            } else {
                WishlistRequest request = new WishlistRequest();
                // Provide SKU id explicitly when adding
                request.setSkuId(skuId);

                wishlistService.addToWishlist(request);
                return ResponseEntity.ok(createSuccessResponse("Product added to wishlist"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    // Helper methods for response formatting
    private Map<String, String> createErrorResponse(String message) {
        return Map.of("error", message);
    }
    
    private Map<String, String> createSuccessResponse(String message) {
        return Map.of("message", message);
    }
}
