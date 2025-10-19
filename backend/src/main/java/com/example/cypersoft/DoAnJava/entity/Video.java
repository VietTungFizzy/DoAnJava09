package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "video_url", nullable = false)
    private String videoUrl;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "duration") // in seconds
    private Integer duration;

    @Column(name = "file_size") // in bytes
    private Long fileSize;

    @Column(name = "video_format", length = 50)
    private String videoFormat; // mp4, avi, mov, etc.

    @Column(name = "resolution", length = 20)
    private String resolution; // 720p, 1080p, 4K, etc.

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // comma-separated tags

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, PROCESSING, DELETED

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    // Product-specific video features
    @Column(name = "video_type")
    private String videoType = "GENERAL"; // PRODUCT_DEMO, TUTORIAL, TESTIMONIAL, UNBOXING, INSTALLATION

    @Column(name = "auto_play")
    private Boolean autoPlay = false;

    @Column(name = "muted_by_default")
    private Boolean mutedByDefault = true;

    @Column(name = "show_controls")
    private Boolean showControls = true;

    @Column(name = "enable_fullscreen")
    private Boolean enableFullscreen = true;

    @Column(name = "enable_subtitles")
    private Boolean enableSubtitles = false;

    @Column(name = "subtitle_url")
    private String subtitleUrl;

    @Column(name = "poster_image_url")
    private String posterImageUrl; // Image shown before video plays

    @Column(name = "video_quality", columnDefinition = "JSON")
    private String videoQuality; // JSON array of available qualities

    // Interactive features
    @Column(name = "has_hotspots")
    private Boolean hasHotspots = false;

    @Column(name = "hotspots_data", columnDefinition = "JSON")
    private String hotspotsData; // JSON data for interactive hotspots

    // Analytics fields
    @Column(name = "completion_rate")
    private Double completionRate = 0.0;

    @Column(name = "average_watch_time")
    private Double averageWatchTime = 0.0; // in seconds

    @Column(name = "engagement_score")
    private Double engagementScore = 0.0;

    @Column(name = "conversion_count")
    private Long conversionCount = 0L; // clicks to product/add to cart

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product relatedProduct;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1L : this.viewCount + 1L;
    }

    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null) ? 1L : this.likeCount + 1L;
    }

    public void decrementLikeCount() {
        this.likeCount = (this.likeCount == null || this.likeCount <= 0) ? 0L : this.likeCount - 1L;
    }

    public String getFormattedDuration() {
        if (duration == null) return "00:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "0 B";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
    }
}
