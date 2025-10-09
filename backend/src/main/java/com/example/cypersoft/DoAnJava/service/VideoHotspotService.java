package com.example.cypersoft.DoAnJava.service;

import com.example.cypersoft.DoAnJava.dto.VideoHotspotRequest;
import com.example.cypersoft.DoAnJava.dto.VideoHotspotResponse;
import com.example.cypersoft.DoAnJava.entity.Product;
import com.example.cypersoft.DoAnJava.entity.User;
import com.example.cypersoft.DoAnJava.entity.Video;
import com.example.cypersoft.DoAnJava.entity.VideoHotspot;
import com.example.cypersoft.DoAnJava.repository.ProductRepository;
import com.example.cypersoft.DoAnJava.repository.UserRepository;
import com.example.cypersoft.DoAnJava.repository.VideoHotspotRepository;
import com.example.cypersoft.DoAnJava.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoHotspotService {

    private final VideoHotspotRepository videoHotspotRepository;
    private final VideoRepository videoRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Create hotspot
    public VideoHotspotResponse createHotspot(VideoHotspotRequest request) {
        User currentUser = getCurrentUser();
        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + request.getVideoId()));

        // Check if user is video owner or admin
        if (!video.getUploadedBy().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to add hotspots to this video");
        }

        // Validate time range
        if (request.getStartTime() >= request.getEndTime()) {
            throw new RuntimeException("Start time must be less than end time");
        }

        VideoHotspot hotspot = new VideoHotspot();
        hotspot.setVideo(video);
        hotspot.setHotspotName(request.getHotspotName());
        hotspot.setStartTime(request.getStartTime());
        hotspot.setEndTime(request.getEndTime());
        hotspot.setXPosition(request.getXPosition());
        hotspot.setYPosition(request.getYPosition());
        hotspot.setWidth(request.getWidth());
        hotspot.setHeight(request.getHeight());
        hotspot.setHotspotType(request.getHotspotType());
        hotspot.setActionUrl(request.getActionUrl());
        hotspot.setPopupContent(request.getPopupContent());
        hotspot.setButtonText(request.getButtonText());
        hotspot.setButtonStyle(request.getButtonStyle());
        hotspot.setIsActive(request.getIsActive());

        // Set target product if provided
        if (request.getTargetProductId() != null) {
            Product targetProduct = productRepository.findById(request.getTargetProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getTargetProductId()));
            hotspot.setTargetProduct(targetProduct);
        }

        VideoHotspot savedHotspot = videoHotspotRepository.save(hotspot);
        return convertToResponse(savedHotspot);
    }

    // Get hotspot by ID
    @Transactional(readOnly = true)
    public VideoHotspotResponse getHotspotById(Integer id) {
        VideoHotspot hotspot = videoHotspotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotspot not found with id: " + id));
        return convertToResponse(hotspot);
    }

    // Get hotspots by video ID
    @Transactional(readOnly = true)
    public List<VideoHotspotResponse> getHotspotsByVideoId(Integer videoId) {
        List<VideoHotspot> hotspots = videoHotspotRepository.findByVideoIdAndIsActiveTrueOrderByStartTime(videoId);
        return hotspots.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get active hotspots at specific time
    @Transactional(readOnly = true)
    public List<VideoHotspotResponse> getActiveHotspotsAtTime(Integer videoId, Double currentTime) {
        List<VideoHotspot> hotspots = videoHotspotRepository.findActiveHotspotsAtTime(videoId, currentTime);
        return hotspots.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Update hotspot
    public VideoHotspotResponse updateHotspot(Integer id, VideoHotspotRequest request) {
        VideoHotspot hotspot = videoHotspotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotspot not found with id: " + id));

        User currentUser = getCurrentUser();
        Video video = hotspot.getVideo();

        // Check if user is video owner or admin
        if (!video.getUploadedBy().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to update this hotspot");
        }

        // Update fields
        if (request.getHotspotName() != null) {
            hotspot.setHotspotName(request.getHotspotName());
        }
        if (request.getStartTime() != null) {
            hotspot.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            hotspot.setEndTime(request.getEndTime());
        }
        if (request.getXPosition() != null) {
            hotspot.setXPosition(request.getXPosition());
        }
        if (request.getYPosition() != null) {
            hotspot.setYPosition(request.getYPosition());
        }
        if (request.getWidth() != null) {
            hotspot.setWidth(request.getWidth());
        }
        if (request.getHeight() != null) {
            hotspot.setHeight(request.getHeight());
        }
        if (request.getHotspotType() != null) {
            hotspot.setHotspotType(request.getHotspotType());
        }
        if (request.getActionUrl() != null) {
            hotspot.setActionUrl(request.getActionUrl());
        }
        if (request.getPopupContent() != null) {
            hotspot.setPopupContent(request.getPopupContent());
        }
        if (request.getButtonText() != null) {
            hotspot.setButtonText(request.getButtonText());
        }
        if (request.getButtonStyle() != null) {
            hotspot.setButtonStyle(request.getButtonStyle());
        }
        if (request.getIsActive() != null) {
            hotspot.setIsActive(request.getIsActive());
        }

        // Update target product
        if (request.getTargetProductId() != null) {
            if (request.getTargetProductId() == 0) {
                hotspot.setTargetProduct(null);
            } else {
                Product targetProduct = productRepository.findById(request.getTargetProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getTargetProductId()));
                hotspot.setTargetProduct(targetProduct);
            }
        }

        VideoHotspot updatedHotspot = videoHotspotRepository.save(hotspot);
        return convertToResponse(updatedHotspot);
    }

    // Delete hotspot
    public void deleteHotspot(Integer id) {
        VideoHotspot hotspot = videoHotspotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotspot not found with id: " + id));

        User currentUser = getCurrentUser();
        Video video = hotspot.getVideo();

        // Check if user is video owner or admin
        if (!video.getUploadedBy().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to delete this hotspot");
        }

        videoHotspotRepository.delete(hotspot);
    }

    // Click hotspot (increment click count)
    public void clickHotspot(Integer id) {
        VideoHotspot hotspot = videoHotspotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotspot not found with id: " + id));

        if (!hotspot.getIsActive()) {
            throw new RuntimeException("Hotspot is not active");
        }

        hotspot.incrementClickCount();
        videoHotspotRepository.save(hotspot);
    }

    // Get hotspots by product
    @Transactional(readOnly = true)
    public List<VideoHotspotResponse> getHotspotsByProduct(Integer productId) {
        List<VideoHotspot> hotspots = videoHotspotRepository.findByTargetProductIdAndIsActiveTrue(productId);
        return hotspots.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get most clicked hotspots
    @Transactional(readOnly = true)
    public List<VideoHotspotResponse> getMostClickedHotspots() {
        List<VideoHotspot> hotspots = videoHotspotRepository.findMostClickedHotspots();
        return hotspots.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get hotspot statistics
    @Transactional(readOnly = true)
    public HotspotStatsResponse getHotspotStatistics(Integer videoId) {
        long totalHotspots = videoHotspotRepository.countByVideoIdAndIsActiveTrue(videoId);
        Long totalClicks = videoHotspotRepository.getTotalClicksByVideoId(videoId);
        
        return new HotspotStatsResponse(
                totalHotspots,
                totalClicks != null ? totalClicks : 0L
        );
    }

    // Helper methods
    private VideoHotspotResponse convertToResponse(VideoHotspot hotspot) {
        VideoHotspotResponse response = new VideoHotspotResponse();
        response.setId(hotspot.getId());
        response.setVideoId(hotspot.getVideo().getId());
        response.setHotspotName(hotspot.getHotspotName());
        response.setStartTime(hotspot.getStartTime());
        response.setEndTime(hotspot.getEndTime());
        response.setXPosition(hotspot.getXPosition());
        response.setYPosition(hotspot.getYPosition());
        response.setWidth(hotspot.getWidth());
        response.setHeight(hotspot.getHeight());
        response.setHotspotType(hotspot.getHotspotType());
        response.setActionUrl(hotspot.getActionUrl());
        response.setPopupContent(hotspot.getPopupContent());
        response.setButtonText(hotspot.getButtonText());
        response.setButtonStyle(hotspot.getButtonStyle());
        response.setIsActive(hotspot.getIsActive());
        response.setClickCount(hotspot.getClickCount());
        response.setCreatedAt(hotspot.getCreatedAt());
        response.setUpdatedAt(hotspot.getUpdatedAt());

        if (hotspot.getTargetProduct() != null) {
            response.setTargetProductId(hotspot.getTargetProduct().getId());
            response.setTargetProductName(hotspot.getTargetProduct().getName());
            response.setTargetProductImageUrl(hotspot.getTargetProduct().getImageUrl());
        }

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

    // Inner class for hotspot statistics
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class HotspotStatsResponse {
        private long totalHotspots;
        private long totalClicks;
    }
}