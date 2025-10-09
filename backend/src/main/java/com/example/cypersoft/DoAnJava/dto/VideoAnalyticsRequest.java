package com.example.cypersoft.DoAnJava.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoAnalyticsRequest {
    
    @NotNull(message = "Video ID is required")
    private Integer videoId;
    
    private Integer userId; // null for anonymous users
    private String sessionId; // for tracking anonymous users
    private String userAgent;
    private String ipAddress;
    private Double watchDuration = 0.0;
    private Double watchPercentage = 0.0;
    private Boolean completed = false;
    private Integer pausedCount = 0;
    private Integer seekedCount = 0;
    private Integer volumeChangedCount = 0;
    private Integer fullscreenCount = 0;
    private Integer qualityChangedCount = 0;
    private String deviceType; // MOBILE, TABLET, DESKTOP
    private String browserType;
    private String referrerUrl;
    private Double exitTime;
    private String conversionAction; // PRODUCT_VIEW, ADD_TO_CART, PURCHASE, HOTSPOT_CLICK
    private Double conversionValue;
}