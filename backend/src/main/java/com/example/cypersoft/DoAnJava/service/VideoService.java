package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.VideoRequest;
import com.example.cypersoft.DoAnJava.dto.VideoResponse;
import com.example.cypersoft.DoAnJava.dto.VideoUpdateRequest;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.entity.Video;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    // Create video
    public VideoResponse createVideo(VideoRequest request) {
        User currentUser = getCurrentUser();
        
        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setVideoUrl(request.getVideoUrl());
        video.setThumbnailUrl(request.getThumbnailUrl());
        video.setDuration(request.getDuration());
        video.setFileSize(request.getFileSize());
        video.setVideoFormat(request.getVideoFormat());
        video.setResolution(request.getResolution());
        video.setCategory(request.getCategory());
        video.setTags(request.getTags());
        video.setIsFeatured(request.getIsFeatured());
        video.setIsPublic(request.getIsPublic());
        video.setStatus(request.getStatus());
        video.setUploadedBy(currentUser);
        
        Video savedVideo = videoRepository.save(video);
        return convertToResponse(savedVideo);
    }

    // Get video by ID
    @Transactional(readOnly = true)
    public VideoResponse getVideoById(Integer id) {
        Video video = videoRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        return convertToResponse(video);
    }

    // Get video by ID and increment view count
    public VideoResponse getVideoByIdAndIncrementViews(Integer id) {
        Video video = videoRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        
        video.incrementViewCount();
        videoRepository.save(video);
        
        return convertToResponse(video);
    }

    // Update video
    public VideoResponse updateVideo(Integer id, VideoUpdateRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        User currentUser = getCurrentUser();
        
        // Check if user is owner or admin
        if (!video.getUploadedBy().getId().equals(currentUser.getId()) && 
            !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to update this video");
        }

        // Update fields if provided
        if (request.getTitle() != null) {
            video.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            video.setDescription(request.getDescription());
        }
        if (request.getThumbnailUrl() != null) {
            video.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getCategory() != null) {
            video.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            video.setTags(request.getTags());
        }
        if (request.getIsFeatured() != null) {
            video.setIsFeatured(request.getIsFeatured());
        }
        if (request.getIsPublic() != null) {
            video.setIsPublic(request.getIsPublic());
        }
        if (request.getStatus() != null) {
            video.setStatus(request.getStatus());
        }

        Video updatedVideo = videoRepository.save(video);
        return convertToResponse(updatedVideo);
    }

    // Delete video (soft delete)
    public void deleteVideo(Integer id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));

        User currentUser = getCurrentUser();
        
        // Check if user is owner or admin
        if (!video.getUploadedBy().getId().equals(currentUser.getId()) && 
            !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to delete this video");
        }

        video.setStatus("DELETED");
        videoRepository.save(video);
    }

    // Get all videos with pagination
    @Transactional(readOnly = true)
    public Page<VideoResponse> getAllVideos(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Video> videos = videoRepository.findByStatus("ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get public videos
    @Transactional(readOnly = true)
    public Page<VideoResponse> getPublicVideos(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Video> videos = videoRepository.findByIsPublicTrueAndStatus("ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get featured videos
    @Transactional(readOnly = true)
    public List<VideoResponse> getFeaturedVideos() {
        List<Video> videos = videoRepository.findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc("ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get videos by category
    @Transactional(readOnly = true)
    public Page<VideoResponse> getVideosByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Video> videos = videoRepository.findByCategoryAndStatus(category, "ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get videos by user
    @Transactional(readOnly = true)
    public Page<VideoResponse> getVideosByUser(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Video> videos = videoRepository.findByUploadedByIdAndStatus(userId, "ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get current user's videos
    @Transactional(readOnly = true)
    public Page<VideoResponse> getCurrentUserVideos(int page, int size) {
        User currentUser = getCurrentUser();
        return getVideosByUser(currentUser.getId(), page, size);
    }

    // Search videos
    @Transactional(readOnly = true)
    public Page<VideoResponse> searchVideos(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Video> videos = videoRepository.searchByTitleOrDescription(keyword, "ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get videos by tag
    @Transactional(readOnly = true)
    public Page<VideoResponse> getVideosByTag(String tag, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Video> videos = videoRepository.findByTagsContaining(tag, "ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get most viewed videos
    @Transactional(readOnly = true)
    public Page<VideoResponse> getMostViewedVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoRepository.findMostViewedVideos("ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get most liked videos
    @Transactional(readOnly = true)
    public Page<VideoResponse> getMostLikedVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoRepository.findMostLikedVideos("ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Get recent videos
    @Transactional(readOnly = true)
    public Page<VideoResponse> getRecentVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videos = videoRepository.findRecentVideos("ACTIVE", pageable);
        return videos.map(this::convertToResponse);
    }

    // Like video
    public void likeVideo(Integer id) {
        Video video = videoRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        
        video.incrementLikeCount();
        videoRepository.save(video);
    }

    // Unlike video
    public void unlikeVideo(Integer id) {
        Video video = videoRepository.findByIdAndStatus(id, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
        
        video.decrementLikeCount();
        videoRepository.save(video);
    }

    // Get all categories
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return videoRepository.findDistinctCategories("ACTIVE");
    }

    // Get video statistics
    @Transactional(readOnly = true)
    public VideoStatsResponse getVideoStatistics() {
        long totalVideos = videoRepository.countByStatus("ACTIVE");
        Long totalViews = videoRepository.getTotalViewsByStatus("ACTIVE");
        Double averageViews = videoRepository.getAverageViewsByStatus("ACTIVE");
        
        return new VideoStatsResponse(
                totalVideos,
                totalViews != null ? totalViews : 0L,
                averageViews != null ? averageViews : 0.0
        );
    }

    // Get videos by product ID
    @Transactional(readOnly = true)
    public List<VideoResponse> getVideosByProduct(Integer productId) {
        List<Video> videos = videoRepository.findByRelatedProductIdAndStatus(productId, "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get product demo videos
    @Transactional(readOnly = true)
    public List<VideoResponse> getProductDemoVideos() {
        List<Video> videos = videoRepository.findByVideoTypeAndStatus("PRODUCT_DEMO", "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get tutorial videos
    @Transactional(readOnly = true)
    public List<VideoResponse> getTutorialVideos() {
        List<Video> videos = videoRepository.findByVideoTypeAndStatus("TUTORIAL", "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get testimonial videos
    @Transactional(readOnly = true)
    public List<VideoResponse> getTestimonialVideos() {
        List<Video> videos = videoRepository.findByVideoTypeAndStatus("TESTIMONIAL", "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get videos by type
    @Transactional(readOnly = true)
    public List<VideoResponse> getVideosByType(String videoType) {
        List<Video> videos = videoRepository.findByVideoTypeAndStatus(videoType, "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get videos with hotspots
    @Transactional(readOnly = true)
    public List<VideoResponse> getVideosWithHotspots() {
        List<Video> videos = videoRepository.findByHasHotspotsTrueAndStatus("ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get videos by engagement score
    @Transactional(readOnly = true)
    public List<VideoResponse> getVideosByEngagementScore(Double minScore) {
        List<Video> videos = videoRepository.findByEngagementScoreGreaterThanEqualAndStatus(minScore, "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get videos by completion rate
    @Transactional(readOnly = true)
    public List<VideoResponse> getVideosByCompletionRate(Double minRate) {
        List<Video> videos = videoRepository.findByCompletionRateGreaterThanEqualAndStatus(minRate, "ACTIVE");
        return videos.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private VideoResponse convertToResponse(Video video) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setVideoUrl(video.getVideoUrl());
        response.setThumbnailUrl(video.getThumbnailUrl());
        response.setDuration(video.getDuration());
        response.setFormattedDuration(video.getFormattedDuration());
        response.setFileSize(video.getFileSize());
        response.setFormattedFileSize(video.getFormattedFileSize());
        response.setVideoFormat(video.getVideoFormat());
        response.setResolution(video.getResolution());
        response.setCategory(video.getCategory());
        
        // Parse tags
        if (video.getTags() != null && !video.getTags().trim().isEmpty()) {
            response.setTags(Arrays.asList(video.getTags().split(",")));
        }
        
        response.setViewCount(video.getViewCount());
        response.setLikeCount(video.getLikeCount());
        response.setStatus(video.getStatus());
        response.setIsFeatured(video.getIsFeatured());
        response.setIsPublic(video.getIsPublic());
        response.setUploadedByName(video.getUploadedBy().getName());
        response.setUploadedById(video.getUploadedBy().getId());
        response.setCreatedAt(video.getCreatedAt());
        response.setUpdatedAt(video.getUpdatedAt());
        
        return response;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && "admin".equalsIgnoreCase(user.getRole().getName());
    }

    // Inner class for video statistics
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class VideoStatsResponse {
        private long totalVideos;
        private long totalViews;
        private double averageViews;
    }
}
