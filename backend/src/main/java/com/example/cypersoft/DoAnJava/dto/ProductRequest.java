package com.example.cypersoft.DoAnJava.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String status; // optional: ACTIVE/INACTIVE
} 