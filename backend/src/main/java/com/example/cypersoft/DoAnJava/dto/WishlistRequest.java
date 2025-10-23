package com.example.cypersoft.DoAnJava.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {
    
    private Integer productId;  // For convenience: will find SKU from product
    
    private Integer skuId;      // Direct SKU reference (preferred)
}
