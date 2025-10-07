package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoUpdateRequest {
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private String thumbnailUrl;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    private String tags; // comma-separated tags

    private Boolean isFeatured;

    private Boolean isPublic;

    private String status; // ACTIVE, INACTIVE, PROCESSING
}
