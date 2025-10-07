package com.example.cypersoft.DoAnJava.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoHotspotRequest {
    
    @NotNull(message = "Video ID is required")
    private Integer videoId;
    
    @NotBlank(message = "Hotspot name is required")
    @Size(max = 255, message = "Hotspot name must not exceed 255 characters")
    private String hotspotName;
    
    @NotNull(message = "Start time is required")
    @DecimalMin(value = "0.0", message = "Start time must be non-negative")
    private Double startTime;
    
    @NotNull(message = "End time is required")
    @DecimalMin(value = "0.0", message = "End time must be non-negative")
    private Double endTime;
    
    @NotNull(message = "X position is required")
    @DecimalMin(value = "0.0", message = "X position must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "X position must be between 0 and 100")
    private Double xPosition;
    
    @NotNull(message = "Y position is required")
    @DecimalMin(value = "0.0", message = "Y position must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Y position must be between 0 and 100")
    private Double yPosition;
    
    @DecimalMin(value = "1.0", message = "Width must be at least 1%")
    @DecimalMax(value = "100.0", message = "Width must not exceed 100%")
    private Double width = 10.0;
    
    @DecimalMin(value = "1.0", message = "Height must be at least 1%")
    @DecimalMax(value = "100.0", message = "Height must not exceed 100%")
    private Double height = 10.0;
    
    @Pattern(regexp = "^(PRODUCT_LINK|ADD_TO_CART|INFO_POPUP|EXTERNAL_LINK)$", 
             message = "Hotspot type must be one of: PRODUCT_LINK, ADD_TO_CART, INFO_POPUP, EXTERNAL_LINK")
    private String hotspotType = "PRODUCT_LINK";
    
    @Size(max = 500, message = "Action URL must not exceed 500 characters")
    private String actionUrl;
    
    @Size(max = 1000, message = "Popup content must not exceed 1000 characters")
    private String popupContent;
    
    @Size(max = 100, message = "Button text must not exceed 100 characters")
    private String buttonText = "Xem chi tiết";
    
    @Pattern(regexp = "^(PRIMARY|SECONDARY|SUCCESS|WARNING|DANGER)$", 
             message = "Button style must be one of: PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER")
    private String buttonStyle = "PRIMARY";
    
    private Boolean isActive = true;
    
    private Integer targetProductId;
}