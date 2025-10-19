package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.VideoAnalyticsRequest;
import com.example.cypersoft.DoAnJava.dto.VideoAnalyticsResponse;
import com.example.cypersoft.DoAnJava.service.VideoAnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video-analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoAnalyticsController {

    private final VideoAnalyticsService videoAnalyticsService;

    // Start video analytics session
    @PostMapping("/start")
    public ResponseEntity<?> startVideoSession(@Valid @RequestBody VideoAnalyticsRequest request) {
        try {
            VideoAnalyticsResponse analytics = videoAnalyticsService.startVideoSession(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Update video analytics
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVideoAnalytics(@PathVariable Integer id, @Valid @RequestBody VideoAnalyticsRequest request) {
        try {
            VideoAnalyticsResponse analytics = videoAnalyticsService.updateVideoAnalytics(id, request);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Update watch progress
    @PostMapping("/{id}/progress")
    public ResponseEntity<?> updateWatchProgress(
            @PathVariable Integer id,
            @RequestParam Double currentTime,
            @RequestParam Double videoDuration) {
        try {
            VideoAnalyticsResponse analytics = videoAnalyticsService.updateWatchProgress(id, currentTime, videoDuration);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Record conversion
    @PostMapping("/{id}/conversion")
    public ResponseEntity<?> recordConversion(
            @PathVariable Integer id,
            @RequestParam String action,
            @RequestParam(required = false) Double value) {
        try {
            VideoAnalyticsResponse analytics = videoAnalyticsService.recordConversion(id, action, value);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get analytics by video ID
    @GetMapping("/video/{videoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnalyticsByVideoId(@PathVariable Integer videoId) {
        try {
            List<VideoAnalyticsResponse> analytics = videoAnalyticsService.getAnalyticsByVideoId(videoId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get analytics by user ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnalyticsByUserId(@PathVariable Integer userId) {
        try {
            List<VideoAnalyticsResponse> analytics = videoAnalyticsService.getAnalyticsByUserId(userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get analytics by session ID
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getAnalyticsBySessionId(@PathVariable String sessionId) {
        try {
            List<VideoAnalyticsResponse> analytics = videoAnalyticsService.getAnalyticsBySessionId(sessionId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get video analytics summary
    @GetMapping("/summary/{videoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getVideoAnalyticsSummary(@PathVariable Integer videoId) {
        try {
            VideoAnalyticsService.VideoAnalyticsSummaryResponse summary = videoAnalyticsService.getVideoAnalyticsSummary(videoId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get most watched videos
    @GetMapping("/most-watched")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMostWatchedVideos() {
        try {
            List<VideoAnalyticsService.MostWatchedVideoResponse> videos = videoAnalyticsService.getMostWatchedVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get most completed videos
    @GetMapping("/most-completed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMostCompletedVideos() {
        try {
            List<VideoAnalyticsService.MostCompletedVideoResponse> videos = videoAnalyticsService.getMostCompletedVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get analytics within date range
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnalyticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<VideoAnalyticsResponse> analytics = videoAnalyticsService.getAnalyticsByDateRange(startDate, endDate);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get analytics by video and date range
    @GetMapping("/video/{videoId}/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnalyticsByVideoAndDateRange(
            @PathVariable Integer videoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<VideoAnalyticsResponse> analytics = videoAnalyticsService.getAnalyticsByVideoAndDateRange(videoId, startDate, endDate);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper methods
    private Map<String, Object> createSuccessResponse(String message) {
        return Map.of(
                "success", true,
                "message", message,
                "timestamp", System.currentTimeMillis()
        );
    }

    private Map<String, Object> createErrorResponse(String message) {
        return Map.of(
                "success", false,
                "message", message,
                "timestamp", System.currentTimeMillis()
        );
    }
}
