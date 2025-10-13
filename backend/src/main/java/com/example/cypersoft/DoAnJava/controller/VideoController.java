package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.VideoRequest;
import com.example.cypersoft.DoAnJava.dto.VideoResponse;
import com.example.cypersoft.DoAnJava.dto.VideoUpdateRequest;
import com.example.cypersoft.DoAnJava.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoController {

    private final VideoService videoService;

    // Create video (authenticated users only)
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createVideo(@Valid @RequestBody VideoRequest request) {
        try {
            VideoResponse video = videoService.createVideo(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(video);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get video by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable Integer id) {
        try {
            VideoResponse video = videoService.getVideoById(id);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Get video by ID and increment view count
    @PostMapping("/{id}/view")
    public ResponseEntity<?> viewVideo(@PathVariable Integer id) {
        try {
            VideoResponse video = videoService.getVideoByIdAndIncrementViews(id);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Update video
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateVideo(@PathVariable Integer id, @Valid @RequestBody VideoUpdateRequest request) {
        try {
            VideoResponse video = videoService.updateVideo(id, request);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Delete video
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteVideo(@PathVariable Integer id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.ok(createSuccessResponse("Video deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get all videos with pagination
    @GetMapping
    public ResponseEntity<?> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<VideoResponse> videos = videoService.getAllVideos(page, size, sortBy, sortDir);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get public videos
    @GetMapping("/public")
    public ResponseEntity<?> getPublicVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Page<VideoResponse> videos = videoService.getPublicVideos(page, size, sortBy, sortDir);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get featured videos
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedVideos() {
        try {
            List<VideoResponse> videos = videoService.getFeaturedVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by category
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getVideosByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getVideosByCategory(category, page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getVideosByUser(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getVideosByUser(userId, page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get current user's videos
    @GetMapping("/my-videos")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentUserVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getCurrentUserVideos(page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Search videos
    @GetMapping("/search")
    public ResponseEntity<?> searchVideos(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.searchVideos(keyword, page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getVideosByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getVideosByTag(tag, page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get most viewed videos
    @GetMapping("/most-viewed")
    public ResponseEntity<?> getMostViewedVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getMostViewedVideos(page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get most liked videos
    @GetMapping("/most-liked")
    public ResponseEntity<?> getMostLikedVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getMostLikedVideos(page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get recent videos
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<VideoResponse> videos = videoService.getRecentVideos(page, size);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Like video
    @PostMapping("/{id}/like")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> likeVideo(@PathVariable Integer id) {
        try {
            videoService.likeVideo(id);
            return ResponseEntity.ok(createSuccessResponse("Video liked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Unlike video
    @PostMapping("/{id}/unlike")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> unlikeVideo(@PathVariable Integer id) {
        try {
            videoService.unlikeVideo(id);
            return ResponseEntity.ok(createSuccessResponse("Video unliked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<String> categories = videoService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get video statistics (admin only)
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getVideoStatistics() {
        try {
            VideoService.VideoStatsResponse stats = videoService.getVideoStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getVideosByProduct(@PathVariable Integer productId) {
        try {
            List<VideoResponse> videos = videoService.getVideosByProduct(productId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get product demo videos
    @GetMapping("/product-demos")
    public ResponseEntity<?> getProductDemoVideos() {
        try {
            List<VideoResponse> videos = videoService.getProductDemoVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get tutorial videos
    @GetMapping("/tutorials")
    public ResponseEntity<?> getTutorialVideos() {
        try {
            List<VideoResponse> videos = videoService.getTutorialVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get testimonial videos
    @GetMapping("/testimonials")
    public ResponseEntity<?> getTestimonialVideos() {
        try {
            List<VideoResponse> videos = videoService.getTestimonialVideos();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by type
    @GetMapping("/type/{videoType}")
    public ResponseEntity<?> getVideosByType(@PathVariable String videoType) {
        try {
            List<VideoResponse> videos = videoService.getVideosByType(videoType);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos with hotspots
    @GetMapping("/with-hotspots")
    public ResponseEntity<?> getVideosWithHotspots() {
        try {
            List<VideoResponse> videos = videoService.getVideosWithHotspots();
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by engagement score
    @GetMapping("/high-engagement")
    public ResponseEntity<?> getVideosByEngagementScore(@RequestParam(defaultValue = "70.0") Double minScore) {
        try {
            List<VideoResponse> videos = videoService.getVideosByEngagementScore(minScore);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get videos by completion rate
    @GetMapping("/high-completion")
    public ResponseEntity<?> getVideosByCompletionRate(@RequestParam(defaultValue = "80.0") Double minRate) {
        try {
            List<VideoResponse> videos = videoService.getVideosByCompletionRate(minRate);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Helper methods
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
