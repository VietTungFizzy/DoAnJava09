package com.example.cypersoft.DoAnJava.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // null for anonymous users

    @Column(name = "session_id")
    private String sessionId; // for tracking anonymous users

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "watch_duration") // actual time watched in seconds
    private Double watchDuration = 0.0;

    @Column(name = "watch_percentage") // percentage of video watched
    private Double watchPercentage = 0.0;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "paused_count")
    private Integer pausedCount = 0;

    @Column(name = "seeked_count")
    private Integer seekedCount = 0;

    @Column(name = "volume_changed_count")
    private Integer volumeChangedCount = 0;

    @Column(name = "fullscreen_count")
    private Integer fullscreenCount = 0;

    @Column(name = "quality_changed_count")
    private Integer qualityChangedCount = 0;

    @Column(name = "device_type")
    private String deviceType; // MOBILE, TABLET, DESKTOP

    @Column(name = "browser_type")
    private String browserType;

    @Column(name = "referrer_url", columnDefinition = "TEXT")
    private String referrerUrl;

    @Column(name = "exit_time") // time when user left the video
    private Double exitTime;

    @Column(name = "conversion_action")
    private String conversionAction; // PRODUCT_VIEW, ADD_TO_CART, PURCHASE, HOTSPOT_CLICK

    @Column(name = "conversion_value")
    private Double conversionValue; // monetary value if applicable

    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Helper methods
    public void markAsCompleted() {
        this.completed = true;
        this.endedAt = LocalDateTime.now();
        this.watchPercentage = 100.0;
    }

    public void updateWatchProgress(double currentTime, double videoDuration) {
        this.exitTime = currentTime;
        this.watchDuration = Math.max(this.watchDuration, currentTime);
        this.watchPercentage = (currentTime / videoDuration) * 100;
        
        if (this.watchPercentage >= 95) { // Consider 95% as completed
            markAsCompleted();
        }
    }

    public void recordConversion(String action, Double value) {
        this.conversionAction = action;
        this.conversionValue = value;
    }
}
