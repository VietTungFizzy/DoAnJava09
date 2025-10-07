package com.example.cypersoft.DoAnJava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private Integer id;
    private String title;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer duration;
    private String formattedDuration;
    private Long fileSize;
    private String formattedFileSize;
    private String videoFormat;
    private String resolution;
    private String category;
    private List<String> tags;
    private Long viewCount;
    private Long likeCount;
    private String status;
    private Boolean isFeatured;
    private Boolean isPublic;
    private String uploadedByName;
    private Integer uploadedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
