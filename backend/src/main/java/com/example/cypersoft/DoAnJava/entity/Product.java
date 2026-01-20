package com.example.cypersoft.DoAnJava.entity;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "slug", unique = true, length = 255)
    private String slug;

    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    // Many-to-one relation to Brand. brandId field owns the column so we mark this as not insertable/updatable.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private Brand brand;

    @Column(name = "status")
    private String status = "active"; // draft, active, inactive

    @Column(name = "visibility")
    private String visibility = "public"; // public, hidden

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Getter
    @ManyToMany
    @JoinTable(
            name="product_categories",
            joinColumns = @JoinColumn(name="product_id"),
            inverseJoinColumns = @JoinColumn(name="category_id")
    )
    private List<Category> categories;

    @Getter
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sku> skus;

    @Getter
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Helper method to get primary image URL from product_images table
    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.stream()
                    .min(Comparator.comparing(ProductImage::getPosition))
                    .map(ProductImage::getImageUrl)
                    .orElse(null);
        }
        return null;
    }

    public String getBrandName() {
        // Return the associated Brand's name if available.
        return this.brand != null ? this.brand.getName() : null;
    }
}
