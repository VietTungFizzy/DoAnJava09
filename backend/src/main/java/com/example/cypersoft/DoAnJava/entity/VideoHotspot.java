package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_hotspots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoHotspot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "hotspot_name", nullable = false)
    private String hotspotName;

    @Column(name = "start_time", nullable = false)
    private Double startTime; // in seconds

    @Column(name = "end_time", nullable = false)
    private Double endTime; // in seconds

    @Column(name = "x_position", nullable = false)
    private Double xPosition; // percentage from left (0-100)

    @Column(name = "y_position", nullable = false)
    private Double yPosition; // percentage from top (0-100)

    @Column(name = "width")
    private Double width = 10.0; // percentage width (0-100)

    @Column(name = "height")
    private Double height = 10.0; // percentage height (0-100)

    @Column(name = "hotspot_type")
    private String hotspotType = "PRODUCT_LINK"; // PRODUCT_LINK, ADD_TO_CART, INFO_POPUP, EXTERNAL_LINK

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "popup_content", columnDefinition = "TEXT")
    private String popupContent;

    @Column(name = "button_text")
    private String buttonText = "Xem chi tiết";

    @Column(name = "button_style")
    private String buttonStyle = "PRIMARY"; // PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_product_id")
    private Product targetProduct;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void incrementClickCount() {
        this.clickCount = (this.clickCount == null) ? 1L : this.clickCount + 1L;
    }

    public boolean isVisibleAtTime(Double currentTime) {
        return currentTime >= startTime && currentTime <= endTime;
    }

    public String getFormattedPosition() {
        return String.format("%.1f%% x %.1f%%", xPosition, yPosition);
    }

    public String getFormattedSize() {
        return String.format("%.1f%% x %.1f%%", width, height);
    }

    public String getFormattedTimeRange() {
        return String.format("%.1fs - %.1fs", startTime, endTime);
    }
}