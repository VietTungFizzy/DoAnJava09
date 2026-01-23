package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    private Order order;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "store_id")
    private Integer storeId;

    @Column(name = "quantity")
    private Integer quantity;

    // SKU id must be present for each order item. Default to 0 for now; will assign real skuId later.
    @Column(name = "sku_id", nullable = false)
    private Integer skuId = 1;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @PrePersist
    protected void onCreate() {
        if (skuId == null) skuId = 1;
    }
}
