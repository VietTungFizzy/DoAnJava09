package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private String productImageUrl;
    private String productStatus;
    private String notes;
    private Integer priority;
    private Boolean isNotified;
    private LocalDateTime createdAt;
    private LocalDateTime productCreatedAt;
    private LocalDateTime productUpdatedAt;
}
