package com.example.cypersoft.DoAnJava.repository;

import com.example.cypersoft.DoAnJava.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    
    // Find videos by status
    Page<Video> findByStatus(String status, Pageable pageable);
    
    // Find public videos
    Page<Video> findByIsPublicTrueAndStatus(String status, Pageable pageable);
    
    // Find featured videos
    List<Video> findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(String status);
    
    // Find videos by category
    Page<Video> findByCategoryAndStatus(String category, String status, Pageable pageable);
    
    // Find videos by user
    Page<Video> findByUploadedByIdAndStatus(Integer userId, String status, Pageable pageable);
    
    // Search videos by title
    @Query("SELECT v FROM Video v WHERE v.title LIKE %:keyword% AND v.status = :status")
    Page<Video> findByTitleContainingAndStatus(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);
    
    // Search videos by title or description
    @Query("SELECT v FROM Video v WHERE (v.title LIKE %:keyword% OR v.description LIKE %:keyword%) AND v.status = :status")
    Page<Video> searchByTitleOrDescription(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);
    
    // Find videos by tags
    @Query("SELECT v FROM Video v WHERE v.tags LIKE %:tag% AND v.status = :status")
    Page<Video> findByTagsContaining(@Param("tag") String tag, @Param("status") String status, Pageable pageable);
    
    // Get most viewed videos
    @Query("SELECT v FROM Video v WHERE v.status = :status ORDER BY v.viewCount DESC")
    Page<Video> findMostViewedVideos(@Param("status") String status, Pageable pageable);
    
    // Get most liked videos
    @Query("SELECT v FROM Video v WHERE v.status = :status ORDER BY v.likeCount DESC")
    Page<Video> findMostLikedVideos(@Param("status") String status, Pageable pageable);
    
    // Get recent videos
    @Query("SELECT v FROM Video v WHERE v.status = :status ORDER BY v.createdAt DESC")
    Page<Video> findRecentVideos(@Param("status") String status, Pageable pageable);
    
    // Count videos by user
    long countByUploadedByIdAndStatus(Integer userId, String status);
    
    // Count videos by category
    long countByCategoryAndStatus(String category, String status);
    
    // Get video statistics
    @Query("SELECT COUNT(v) FROM Video v WHERE v.status = :status")
    long countByStatus(@Param("status") String status);
    
    @Query("SELECT SUM(v.viewCount) FROM Video v WHERE v.status = :status")
    Long getTotalViewsByStatus(@Param("status") String status);
    
    @Query("SELECT AVG(v.viewCount) FROM Video v WHERE v.status = :status")
    Double getAverageViewsByStatus(@Param("status") String status);
    
    // Find video by ID and status
    Optional<Video> findByIdAndStatus(Integer id, String status);
    
    // Get all categories
    @Query("SELECT DISTINCT v.category FROM Video v WHERE v.category IS NOT NULL AND v.status = :status ORDER BY v.category")
    List<String> findDistinctCategories(@Param("status") String status);
    
    // Find videos by product ID
    @Query("SELECT v FROM Video v WHERE v.relatedProduct.id = :productId AND v.status = :status")
    List<Video> findByRelatedProductIdAndStatus(@Param("productId") Integer productId, @Param("status") String status);
    
    // Find videos by video type
    @Query("SELECT v FROM Video v WHERE v.videoType = :videoType AND v.status = :status")
    List<Video> findByVideoTypeAndStatus(@Param("videoType") String videoType, @Param("status") String status);
    
    // Find videos with hotspots
    @Query("SELECT v FROM Video v WHERE v.hasHotspots = true AND v.status = :status")
    List<Video> findByHasHotspotsTrueAndStatus(@Param("status") String status);
    
    // Find videos by engagement score
    @Query("SELECT v FROM Video v WHERE v.engagementScore >= :minScore AND v.status = :status ORDER BY v.engagementScore DESC")
    List<Video> findByEngagementScoreGreaterThanEqualAndStatus(@Param("minScore") Double minScore, @Param("status") String status);
    
    // Find videos by completion rate
    @Query("SELECT v FROM Video v WHERE v.completionRate >= :minRate AND v.status = :status ORDER BY v.completionRate DESC")
    List<Video> findByCompletionRateGreaterThanEqualAndStatus(@Param("minRate") Double minRate, @Param("status") String status);
    
    // Find videos by conversion count
    @Query("SELECT v FROM Video v WHERE v.conversionCount >= :minCount AND v.status = :status ORDER BY v.conversionCount DESC")
    List<Video> findByConversionCountGreaterThanEqualAndStatus(@Param("minCount") Long minCount, @Param("status") String status);
    
    // Find videos by average watch time
    @Query("SELECT v FROM Video v WHERE v.averageWatchTime >= :minTime AND v.status = :status ORDER BY v.averageWatchTime DESC")
    List<Video> findByAverageWatchTimeGreaterThanEqualAndStatus(@Param("minTime") Double minTime, @Param("status") String status);
    
    // Get video types
    @Query("SELECT DISTINCT v.videoType FROM Video v WHERE v.videoType IS NOT NULL AND v.status = :status ORDER BY v.videoType")
    List<String> findDistinctVideoTypes(@Param("status") String status);
    
    // Get videos by multiple criteria
    @Query("SELECT v FROM Video v WHERE " +
           "(:category IS NULL OR v.category = :category) AND " +
           "(:videoType IS NULL OR v.videoType = :videoType) AND " +
           "(:hasHotspots IS NULL OR v.hasHotspots = :hasHotspots) AND " +
           "(:isFeatured IS NULL OR v.isFeatured = :isFeatured) AND " +
           "v.status = :status " +
           "ORDER BY v.createdAt DESC")
    List<Video> findByMultipleCriteria(@Param("category") String category, 
                                      @Param("videoType") String videoType,
                                      @Param("hasHotspots") Boolean hasHotspots,
                                      @Param("isFeatured") Boolean isFeatured,
                                      @Param("status") String status);
}
