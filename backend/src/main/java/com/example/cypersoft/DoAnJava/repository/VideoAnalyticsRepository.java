package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.VideoAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VideoAnalyticsRepository extends JpaRepository<VideoAnalytics, Integer> {
    
    // Find analytics by video ID
    List<VideoAnalytics> findByVideoIdOrderByStartedAtDesc(Integer videoId);
    
    // Find analytics by user ID
    List<VideoAnalytics> findByUserIdOrderByStartedAtDesc(Integer userId);
    
    // Find analytics by session ID
    List<VideoAnalytics> findBySessionIdOrderByStartedAtDesc(String sessionId);
    
    // Find completed analytics
    List<VideoAnalytics> findByCompletedTrueOrderByStartedAtDesc();
    
    // Find analytics by device type
    List<VideoAnalytics> findByDeviceTypeOrderByStartedAtDesc(String deviceType);
    
    // Find analytics by conversion action
    List<VideoAnalytics> findByConversionActionOrderByStartedAtDesc(String conversionAction);
    
    // Get analytics for a specific video and user
    VideoAnalytics findByVideoIdAndUserId(Integer videoId, Integer userId);
    
    // Get analytics for a specific video and session
    VideoAnalytics findByVideoIdAndSessionId(Integer videoId, String sessionId);
    
    // Get total views for a video
    @Query("SELECT COUNT(va) FROM VideoAnalytics va WHERE va.video.id = :videoId")
    Long getTotalViewsByVideoId(@Param("videoId") Integer videoId);
    
    // Get total completed views for a video
    @Query("SELECT COUNT(va) FROM VideoAnalytics va WHERE va.video.id = :videoId AND va.completed = true")
    Long getCompletedViewsByVideoId(@Param("videoId") Integer videoId);
    
    // Get average watch time for a video
    @Query("SELECT AVG(va.watchDuration) FROM VideoAnalytics va WHERE va.video.id = :videoId")
    Double getAverageWatchTimeByVideoId(@Param("videoId") Integer videoId);
    
    // Get average watch percentage for a video
    @Query("SELECT AVG(va.watchPercentage) FROM VideoAnalytics va WHERE va.video.id = :videoId")
    Double getAverageWatchPercentageByVideoId(@Param("videoId") Integer videoId);
    
    // Get conversion count for a video
    @Query("SELECT COUNT(va) FROM VideoAnalytics va WHERE va.video.id = :videoId AND va.conversionAction IS NOT NULL")
    Long getConversionCountByVideoId(@Param("videoId") Integer videoId);
    
    // Get analytics within date range
    @Query("SELECT va FROM VideoAnalytics va WHERE va.startedAt BETWEEN :startDate AND :endDate ORDER BY va.startedAt DESC")
    List<VideoAnalytics> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get analytics by video and date range
    @Query("SELECT va FROM VideoAnalytics va WHERE va.video.id = :videoId AND va.startedAt BETWEEN :startDate AND :endDate ORDER BY va.startedAt DESC")
    List<VideoAnalytics> findByVideoIdAndDateRange(@Param("videoId") Integer videoId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Get most watched videos
    @Query("SELECT va.video.id, COUNT(va) as viewCount FROM VideoAnalytics va GROUP BY va.video.id ORDER BY viewCount DESC")
    List<Object[]> getMostWatchedVideos();
    
    // Get most completed videos
    @Query("SELECT va.video.id, COUNT(va) as completionCount FROM VideoAnalytics va WHERE va.completed = true GROUP BY va.video.id ORDER BY completionCount DESC")
    List<Object[]> getMostCompletedVideos();
    
    // Get analytics summary for a video
    @Query("SELECT " +
           "COUNT(va) as totalViews, " +
           "COUNT(CASE WHEN va.completed = true THEN 1 END) as completedViews, " +
           "AVG(va.watchDuration) as avgWatchTime, " +
           "AVG(va.watchPercentage) as avgWatchPercentage, " +
           "COUNT(CASE WHEN va.conversionAction IS NOT NULL THEN 1 END) as conversions " +
           "FROM VideoAnalytics va WHERE va.video.id = :videoId")
    Object[] getVideoAnalyticsSummary(@Param("videoId") Integer videoId);
}