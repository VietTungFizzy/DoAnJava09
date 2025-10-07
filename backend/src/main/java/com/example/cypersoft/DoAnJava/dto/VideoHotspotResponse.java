package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoHotspotResponse {
    private Integer id;
    private Integer videoId;
    private String hotspotName;
    private Double startTime;
    private Double endTime;
    private Double xPosition;
    private Double yPosition;
    private Double width;
    private Double height;
    private String hotspotType;
    private String actionUrl;
    private String popupContent;
    private String buttonText;
    private String buttonStyle;
    private Boolean isActive;
    private Long clickCount;
    private Integer targetProductId;
    private String targetProductName;
    private String targetProductImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
