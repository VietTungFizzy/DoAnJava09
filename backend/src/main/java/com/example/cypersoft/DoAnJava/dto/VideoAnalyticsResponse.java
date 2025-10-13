package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoAnalyticsResponse {
    private Integer id;
    private Integer videoId;
    private String videoTitle;
    private Integer userId;
    private String userName;
    private String sessionId;
    private String userAgent;
    private String ipAddress;
    private Double watchDuration;
    private Double watchPercentage;
    private Boolean completed;
    private Integer pausedCount;
    private Integer seekedCount;
    private Integer volumeChangedCount;
    private Integer fullscreenCount;
    private Integer qualityChangedCount;
    private String deviceType;
    private String browserType;
    private String referrerUrl;
    private Double exitTime;
    private String conversionAction;
    private Double conversionValue;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
}
