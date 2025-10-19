package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.VideoHotspot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoHotspotRepository extends JpaRepository<VideoHotspot, Integer> {
    
    // Find hotspots by video ID
    List<VideoHotspot> findByVideoIdAndIsActiveTrueOrderByStartTime(Integer videoId);
    
    // Find hotspots by video ID and time range
    @Query("SELECT h FROM VideoHotspot h WHERE h.video.id = :videoId AND h.isActive = true AND :currentTime BETWEEN h.startTime AND h.endTime")
    List<VideoHotspot> findActiveHotspotsAtTime(@Param("videoId") Integer videoId, @Param("currentTime") Double currentTime);
    
    // Find hotspots by target product
    List<VideoHotspot> findByTargetProductIdAndIsActiveTrue(Integer productId);
    
    // Find hotspots by type
    List<VideoHotspot> findByHotspotTypeAndIsActiveTrueOrderByClickCountDesc(String hotspotType);
    
    // Get most clicked hotspots
    @Query("SELECT h FROM VideoHotspot h WHERE h.isActive = true ORDER BY h.clickCount DESC")
    List<VideoHotspot> findMostClickedHotspots();
    
    // Count hotspots by video
    long countByVideoIdAndIsActiveTrue(Integer videoId);
    
    // Get total clicks for a video
    @Query("SELECT SUM(h.clickCount) FROM VideoHotspot h WHERE h.video.id = :videoId AND h.isActive = true")
    Long getTotalClicksByVideoId(@Param("videoId") Integer videoId);
}
