package com.example.cypersoft.DoAnJava.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;
    private Integer storeId;
    private String name;
    private String slug;
    private String description;
    private String status;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String brandName;
    private Set<String> categoryNames;
    
    // Price and stock from the first active SKU
    private BigDecimal price;
    private Integer stock;
    private String imageUrl; // Will be populated from product_images table
    private boolean isInWishlist; // new field to indicate if current user has this product in wishlist
}
