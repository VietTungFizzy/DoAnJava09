package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Video URL is required")
    private String videoUrl;

    private String thumbnailUrl;

    private Integer duration; // in seconds

    private Long fileSize; // in bytes

    @Size(max = 50, message = "Video format must not exceed 50 characters")
    private String videoFormat;

    @Size(max = 20, message = "Resolution must not exceed 20 characters")
    private String resolution;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    private String tags; // comma-separated tags

    private Boolean isFeatured = false;

    private Boolean isPublic = true;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE, PROCESSING

    // Product-specific fields
    private String videoType = "GENERAL"; // PRODUCT_DEMO, TUTORIAL, TESTIMONIAL, UNBOXING, INSTALLATION

    private Boolean autoPlay = false;

    private Boolean mutedByDefault = true;

    private Boolean showControls = true;

    private Boolean enableFullscreen = true;

    private Boolean enableSubtitles = false;

    private String subtitleUrl;

    private String posterImageUrl;

    private Integer relatedProductId;

    // Interactive features
    private Boolean hasHotspots = false;
}
