package com.example.cypersoft.DoAnJava.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private Integer storeId;
    private String name;
    private String slug;
    private String description;
    private String status; // draft, active, inactive
    private String visibility; // public, hidden
    private Integer brandId;
    // Note: price, stock, and imageUrl are now handled in SKU and product_images tables
    // This would need to be updated to handle SKU creation/updates
} 