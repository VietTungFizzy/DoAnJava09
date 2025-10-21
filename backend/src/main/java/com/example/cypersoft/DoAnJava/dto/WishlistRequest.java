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
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    @NotNull(message = "Priority is required")
    private Integer priority = 1; // 1 = low, 2 = medium, 3 = high
    
    private Boolean isNotified = false;
}
