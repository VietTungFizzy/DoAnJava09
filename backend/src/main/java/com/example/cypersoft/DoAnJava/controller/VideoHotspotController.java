package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.VideoHotspotRequest;
import com.example.cypersoft.DoAnJava.dto.VideoHotspotResponse;
import com.example.cypersoft.DoAnJava.service.VideoHotspotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video-hotspots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoHotspotController {

    private final VideoHotspotService videoHotspotService;

    // Create hotspot
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createHotspot(@Valid @RequestBody VideoHotspotRequest request) {
        try {
            VideoHotspotResponse hotspot = videoHotspotService.createHotspot(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(hotspot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get hotspot by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotspotById(@PathVariable Integer id) {
        try {
            VideoHotspotResponse hotspot = videoHotspotService.getHotspotById(id);
            return ResponseEntity.ok(hotspot);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    // Get hotspots by video ID
    @GetMapping("/video/{videoId}")
    public ResponseEntity<?> getHotspotsByVideoId(@PathVariable Integer videoId) {
        try {
            List<VideoHotspotResponse> hotspots = videoHotspotService.getHotspotsByVideoId(videoId);
            return ResponseEntity.ok(hotspots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get active hotspots at specific time
    @GetMapping("/video/{videoId}/active")
    public ResponseEntity<?> getActiveHotspotsAtTime(
            @PathVariable Integer videoId,
            @RequestParam Double currentTime) {
        try {
            List<VideoHotspotResponse> hotspots = videoHotspotService.getActiveHotspotsAtTime(videoId, currentTime);
            return ResponseEntity.ok(hotspots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Update hotspot
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateHotspot(@PathVariable Integer id, @Valid @RequestBody VideoHotspotRequest request) {
        try {
            VideoHotspotResponse hotspot = videoHotspotService.updateHotspot(id, request);
            return ResponseEntity.ok(hotspot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Delete hotspot
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteHotspot(@PathVariable Integer id) {
        try {
            videoHotspotService.deleteHotspot(id);
            return ResponseEntity.ok(createSuccessResponse("Hotspot deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Click hotspot
    @PostMapping("/{id}/click")
    public ResponseEntity<?> clickHotspot(@PathVariable Integer id) {
        try {
            videoHotspotService.clickHotspot(id);
            return ResponseEntity.ok(createSuccessResponse("Hotspot clicked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get hotspots by product
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getHotspotsByProduct(@PathVariable Integer productId) {
        try {
            List<VideoHotspotResponse> hotspots = videoHotspotService.getHotspotsByProduct(productId);
            return ResponseEntity.ok(hotspots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get most clicked hotspots
    @GetMapping("/most-clicked")
    public ResponseEntity<?> getMostClickedHotspots() {
        try {
            List<VideoHotspotResponse> hotspots = videoHotspotService.getMostClickedHotspots();
            return ResponseEntity.ok(hotspots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    // Get hotspot statistics
    @GetMapping("/statistics/{videoId}")
    public ResponseEntity<?> getHotspotStatistics(@PathVariable Integer videoId) {
        try {
            VideoHotspotService.HotspotStatsResponse stats = videoHotspotService.getHotspotStatistics(videoId);
            return ResponseEntity.ok(stats);
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
