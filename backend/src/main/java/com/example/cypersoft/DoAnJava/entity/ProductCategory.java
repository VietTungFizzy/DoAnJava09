package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_categories")
public class ProductCategory {
    @EmbeddedId
    private ProductCategoryId id;


    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
