package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "skus")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sku_code", unique = true, length = 100)
    private String skuCode;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 12, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "weight_gram")
    private Integer weightGram = 0;

    @Column(name = "length_mm")
    private Integer lengthMm = 0;

    @Column(name = "width_mm")
    private Integer widthMm = 0;

    @Column(name = "height_mm")
    private Integer heightMm = 0;

    @Column(name = "status")
    private String status = "active"; // active, inactive

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper method to get primary image
    // Note: Should be populated from sku_images or product_images table via repository
    private String imageUrl;
    
    // Helper method to get product name  
    public String getProductName() {
        return product != null ? product.getName() : null;
    }
    
    public String getImageUrl() {
        return this.imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

