package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.VideoAnalyticsRequest;
import com.example.cypersoft.DoAnJava.dto.VideoAnalyticsResponse;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.entity.Video;
import com.example.cypersoft.DoAnJava.entity.VideoAnalytics;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
import com.example.cypersoft.DoAnJava.repository.VideoAnalyticsRepository;
import com.example.cypersoft.DoAnJava.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoAnalyticsService {

    private final VideoAnalyticsRepository videoAnalyticsRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    // Start video analytics session
    public VideoAnalyticsResponse startVideoSession(VideoAnalyticsRequest request) {
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + request.getVideoId()));

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElse(null);
        }

        VideoAnalytics analytics = new VideoAnalytics();
        analytics.setVideo(video);
        analytics.setUser(user);
        analytics.setSessionId(request.getSessionId());
        analytics.setUserAgent(request.getUserAgent());
        analytics.setIpAddress(request.getIpAddress());
        analytics.setDeviceType(request.getDeviceType());
        analytics.setBrowserType(request.getBrowserType());
        analytics.setReferrerUrl(request.getReferrerUrl());
        analytics.setStartedAt(LocalDateTime.now());

        VideoAnalytics savedAnalytics = videoAnalyticsRepository.save(analytics);
        return convertToResponse(savedAnalytics);
    }

    // Update video analytics
    public VideoAnalyticsResponse updateVideoAnalytics(Integer id, VideoAnalyticsRequest request) {
        VideoAnalytics analytics = videoAnalyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics session not found with id: " + id));

        // Update fields
        if (request.getWatchDuration() != null) {
            analytics.setWatchDuration(request.getWatchDuration());
        }
        if (request.getWatchPercentage() != null) {
            analytics.setWatchPercentage(request.getWatchPercentage());
        }
        if (request.getCompleted() != null) {
            analytics.setCompleted(request.getCompleted());
            if (request.getCompleted()) {
                analytics.markAsCompleted();
            }
        }
        if (request.getPausedCount() != null) {
            analytics.setPausedCount(request.getPausedCount());
        }
        if (request.getSeekedCount() != null) {
            analytics.setSeekedCount(request.getSeekedCount());
        }
        if (request.getVolumeChangedCount() != null) {
            analytics.setVolumeChangedCount(request.getVolumeChangedCount());
        }
        if (request.getFullscreenCount() != null) {
            analytics.setFullscreenCount(request.getFullscreenCount());
        }
        if (request.getQualityChangedCount() != null) {
            analytics.setQualityChangedCount(request.getQualityChangedCount());
        }
        if (request.getExitTime() != null) {
            analytics.setExitTime(request.getExitTime());
        }
        if (request.getConversionAction() != null) {
            analytics.setConversionAction(request.getConversionAction());
        }
        if (request.getConversionValue() != null) {
            analytics.setConversionValue(request.getConversionValue());
        }

        VideoAnalytics updatedAnalytics = videoAnalyticsRepository.save(analytics);
        return convertToResponse(updatedAnalytics);
    }

    // Update watch progress
    public VideoAnalyticsResponse updateWatchProgress(Integer id, Double currentTime, Double videoDuration) {
        VideoAnalytics analytics = videoAnalyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics session not found with id: " + id));

        analytics.updateWatchProgress(currentTime, videoDuration);
        VideoAnalytics updatedAnalytics = videoAnalyticsRepository.save(analytics);
        return convertToResponse(updatedAnalytics);
    }

    // Record conversion
    public VideoAnalyticsResponse recordConversion(Integer id, String action, Double value) {
        VideoAnalytics analytics = videoAnalyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics session not found with id: " + id));

        analytics.recordConversion(action, value);
        VideoAnalytics updatedAnalytics = videoAnalyticsRepository.save(analytics);
        return convertToResponse(updatedAnalytics);
    }

    // Get analytics by video ID
    @Transactional(readOnly = true)
    public List<VideoAnalyticsResponse> getAnalyticsByVideoId(Integer videoId) {
        List<VideoAnalytics> analytics = videoAnalyticsRepository.findByVideoIdOrderByStartedAtDesc(videoId);
        return analytics.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get analytics by user ID
    @Transactional(readOnly = true)
    public List<VideoAnalyticsResponse> getAnalyticsByUserId(Integer userId) {
        List<VideoAnalytics> analytics = videoAnalyticsRepository.findByUserIdOrderByStartedAtDesc(userId);
        return analytics.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get analytics by session ID
    @Transactional(readOnly = true)
    public List<VideoAnalyticsResponse> getAnalyticsBySessionId(String sessionId) {
        List<VideoAnalytics> analytics = videoAnalyticsRepository.findBySessionIdOrderByStartedAtDesc(sessionId);
        return analytics.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get video analytics summary
    @Transactional(readOnly = true)
    public VideoAnalyticsSummaryResponse getVideoAnalyticsSummary(Integer videoId) {
        Object[] summary = videoAnalyticsRepository.getVideoAnalyticsSummary(videoId);
        
        if (summary == null || summary.length == 0) {
            return new VideoAnalyticsSummaryResponse(0L, 0L, 0.0, 0.0, 0L);
        }

        Long totalViews = ((Number) summary[0]).longValue();
        Long completedViews = ((Number) summary[1]).longValue();
        Double avgWatchTime = summary[2] != null ? ((Number) summary[2]).doubleValue() : 0.0;
        Double avgWatchPercentage = summary[3] != null ? ((Number) summary[3]).doubleValue() : 0.0;
        Long conversions = ((Number) summary[4]).longValue();

        return new VideoAnalyticsSummaryResponse(totalViews, completedViews, avgWatchTime, avgWatchPercentage, conversions);
    }

    // Get most watched videos
    @Transactional(readOnly = true)
    public List<MostWatchedVideoResponse> getMostWatchedVideos() {
        List<Object[]> results = videoAnalyticsRepository.getMostWatchedVideos();
        return results.stream()
                .map(result -> new MostWatchedVideoResponse(
                        ((Number) result[0]).intValue(),
                        ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // Get most completed videos
    @Transactional(readOnly = true)
    public List<MostCompletedVideoResponse> getMostCompletedVideos() {
        List<Object[]> results = videoAnalyticsRepository.getMostCompletedVideos();
        return results.stream()
                .map(result -> new MostCompletedVideoResponse(
                        ((Number) result[0]).intValue(),
                        ((Number) result[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    // Get analytics within date range
    @Transactional(readOnly = true)
    public List<VideoAnalyticsResponse> getAnalyticsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<VideoAnalytics> analytics = videoAnalyticsRepository.findByDateRange(startDate, endDate);
        return analytics.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get analytics by video and date range
    @Transactional(readOnly = true)
    public List<VideoAnalyticsResponse> getAnalyticsByVideoAndDateRange(Integer videoId, LocalDateTime startDate, LocalDateTime endDate) {
        List<VideoAnalytics> analytics = videoAnalyticsRepository.findByVideoIdAndDateRange(videoId, startDate, endDate);
        return analytics.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private VideoAnalyticsResponse convertToResponse(VideoAnalytics analytics) {
        VideoAnalyticsResponse response = new VideoAnalyticsResponse();
        response.setId(analytics.getId());
        response.setVideoId(analytics.getVideo().getId());
        response.setVideoTitle(analytics.getVideo().getTitle());
        response.setUserId(analytics.getUser() != null ? analytics.getUser().getId() : null);
        response.setUserName(analytics.getUser() != null ? analytics.getUser().getName() : null);
        response.setSessionId(analytics.getSessionId());
        response.setUserAgent(analytics.getUserAgent());
        response.setIpAddress(analytics.getIpAddress());
        response.setWatchDuration(analytics.getWatchDuration());
        response.setWatchPercentage(analytics.getWatchPercentage());
        response.setCompleted(analytics.getCompleted());
        response.setPausedCount(analytics.getPausedCount());
        response.setSeekedCount(analytics.getSeekedCount());
        response.setVolumeChangedCount(analytics.getVolumeChangedCount());
        response.setFullscreenCount(analytics.getFullscreenCount());
        response.setQualityChangedCount(analytics.getQualityChangedCount());
        response.setDeviceType(analytics.getDeviceType());
        response.setBrowserType(analytics.getBrowserType());
        response.setReferrerUrl(analytics.getReferrerUrl());
        response.setExitTime(analytics.getExitTime());
        response.setConversionAction(analytics.getConversionAction());
        response.setConversionValue(analytics.getConversionValue());
        response.setStartedAt(analytics.getStartedAt());
        response.setEndedAt(analytics.getEndedAt());
        response.setCreatedAt(analytics.getCreatedAt());
        return response;
    }

    // Response classes
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class VideoAnalyticsSummaryResponse {
        private Long totalViews;
        private Long completedViews;
        private Double avgWatchTime;
        private Double avgWatchPercentage;
        private Long conversions;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class MostWatchedVideoResponse {
        private Integer videoId;
        private Long viewCount;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class MostCompletedVideoResponse {
        private Integer videoId;
        private Long completionCount;
    }
}
