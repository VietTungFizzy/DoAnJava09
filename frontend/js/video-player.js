/**
 * Enhanced Video Player for E-commerce Product Demos
 * Features: Interactive hotspots, analytics tracking, responsive design
 */

class ProductVideoPlayer {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.video = this.container.querySelector('#productVideo');
        this.options = {
            apiBaseUrl: options.apiBaseUrl || 'http://localhost:8081/api',
            autoPlay: options.autoPlay || false,
            muted: options.muted || true,
            showControls: options.showControls !== false,
            enableHotspots: options.enableHotspots !== false,
            enableAnalytics: options.enableAnalytics !== false,
            ...options
        };

        this.currentVideo = null;
        this.hotspots = [];
        this.analytics = {
            startTime: null,
            watchDuration: 0,
            pauseCount: 0,
            seekCount: 0,
            volumeChangeCount: 0,
            fullscreenCount: 0,
            lastPosition: 0
        };

        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupControls();
        this.detectDevice();
    }

    // Load and play video
    async loadVideo(videoId) {
        try {
            this.showLoading(true);
            
            // Fetch video data from API
            const response = await fetch(`${this.options.apiBaseUrl}/videos/${videoId}/view`, {
                method: 'POST'
            });
            
            if (!response.ok) throw new Error('Failed to load video');
            
            this.currentVideo = await response.json();
            this.setupVideo();
            
            // Load hotspots if enabled
            if (this.options.enableHotspots && this.currentVideo.hasHotspots) {
                await this.loadHotspots(videoId);
            }
            
            this.showLoading(false);
            this.updateVideoInfo();
            
            // Start analytics tracking
            if (this.options.enableAnalytics) {
                this.startAnalyticsTracking();
            }
            
        } catch (error) {
            console.error('Error loading video:', error);
            this.showError();
        }
    }

    setupVideo() {
        const video = this.video;
        const videoData = this.currentVideo;

        // Set video source
        video.src = videoData.videoUrl;
        
        // Set poster image
        if (videoData.posterImageUrl || videoData.thumbnailUrl) {
            video.poster = videoData.posterImageUrl || videoData.thumbnailUrl;
        }

        // Configure video attributes
        video.muted = this.options.muted || videoData.mutedByDefault;
        video.controls = false; // Use custom controls
        video.preload = 'metadata';

        // Set subtitles if available
        if (videoData.enableSubtitles && videoData.subtitleUrl) {
            const track = video.querySelector('track');
            track.src = videoData.subtitleUrl;
        }

        // Auto-play if enabled (only on homepage with muted)
        if (this.options.autoPlay || videoData.autoPlay) {
            video.muted = true;
            video.play().catch(e => console.log('Auto-play failed:', e));
        }
    }

    async loadHotspots(videoId) {
        try {
            const response = await fetch(`${this.options.apiBaseUrl}/video-hotspots/video/${videoId}`);
            if (response.ok) {
                this.hotspots = await response.json();
                this.renderHotspots();
            }
        } catch (error) {
            console.error('Error loading hotspots:', error);
        }
    }

    renderHotspots() {
        // Clear existing hotspots
        this.container.querySelectorAll('.hotspot').forEach(hotspot => hotspot.remove());
        
        if (!this.hotspots || this.hotspots.length === 0) return;

        this.hotspots.forEach(hotspot => {
            const hotspotElement = this.createHotspotElement(hotspot);
            this.container.appendChild(hotspotElement);
        });
    }

    createHotspotElement(hotspot) {
        const hotspotDiv = document.createElement('div');
        hotspotDiv.className = 'hotspot';
        hotspotDiv.dataset.hotspotId = hotspot.id;
        hotspotDiv.dataset.startTime = hotspot.startTime;
        hotspotDiv.dataset.endTime = hotspot.endTime;
        hotspotDiv.style.position = 'absolute';
        hotspotDiv.style.left = `${hotspot.xPosition}%`;
        hotspotDiv.style.top = `${hotspot.yPosition}%`;
        hotspotDiv.style.width = `${hotspot.width}%`;
        hotspotDiv.style.height = `${hotspot.height}%`;
        hotspotDiv.style.pointerEvents = 'none';
        hotspotDiv.style.zIndex = '10';

        const button = document.createElement('button');
        button.className = `hotspot-button btn btn-${hotspot.buttonStyle.toLowerCase()}`;
        button.textContent = hotspot.buttonText;
        button.style.position = 'absolute';
        button.style.left = '50%';
        button.style.top = '50%';
        button.style.transform = 'translate(-50%, -50%)';
        button.style.pointerEvents = 'auto';
        button.style.fontSize = '12px';
        button.style.padding = '4px 8px';
        button.style.borderRadius = '4px';
        button.style.border = 'none';
        button.style.cursor = 'pointer';
        button.style.opacity = '0.9';
        button.style.transition = 'opacity 0.3s ease';

        // Add hover effects
        button.addEventListener('mouseenter', () => {
            button.style.opacity = '1';
            button.style.transform = 'translate(-50%, -50%) scale(1.05)';
        });

        button.addEventListener('mouseleave', () => {
            button.style.opacity = '0.9';
            button.style.transform = 'translate(-50%, -50%) scale(1)';
        });

        hotspotDiv.appendChild(button);
        return hotspotDiv;
    }

    updateHotspotsVisibility() {
        if (!this.hotspots || this.hotspots.length === 0) return;

        const currentTime = this.video.currentTime;
        
        this.container.querySelectorAll('.hotspot').forEach(hotspotElement => {
            const startTime = parseFloat(hotspotElement.dataset.startTime);
            const endTime = parseFloat(hotspotElement.dataset.endTime);
            
            if (currentTime >= startTime && currentTime <= endTime) {
                hotspotElement.style.display = 'block';
                hotspotElement.style.opacity = '1';
            } else {
                hotspotElement.style.display = 'none';
                hotspotElement.style.opacity = '0';
            }
        });
    }

    setupEventListeners() {
        const video = this.video;

        // Video events
        video.addEventListener('loadedmetadata', () => this.onVideoLoaded());
        video.addEventListener('timeupdate', () => this.onTimeUpdate());
        video.addEventListener('play', () => this.onPlay());
        video.addEventListener('pause', () => this.onPause());
        video.addEventListener('ended', () => this.onVideoEnded());
        video.addEventListener('error', () => this.showError());
        video.addEventListener('volumechange', () => this.onVolumeChange());

        // Control events
        this.setupControlEvents();

        // Hotspot events
        this.container.addEventListener('click', (e) => {
            if (e.target.classList.contains('hotspot-button')) {
                this.handleHotspotClick(e.target);
            }
        });

        // Keyboard shortcuts
        document.addEventListener('keydown', (e) => this.handleKeyboard(e));

        // Mouse/touch events for showing/hiding controls
        this.container.addEventListener('mouseenter', () => this.showControls());
        this.container.addEventListener('mouseleave', () => this.hideControls());
        this.container.addEventListener('mousemove', () => this.showControls());
    }

    setupControlEvents() {
        // Play/Pause button
        const playPauseBtn = this.container.querySelector('#playPauseBtn');
        playPauseBtn.addEventListener('click', () => this.togglePlayPause());

        // Progress bar
        const progressBar = this.container.querySelector('.progress-bar');
        progressBar.addEventListener('click', (e) => this.seekTo(e));

        // Volume controls
        const muteBtn = this.container.querySelector('#muteBtn');
        const volumeSlider = this.container.querySelector('#volumeSlider');
        muteBtn.addEventListener('click', () => this.toggleMute());
        volumeSlider.addEventListener('input', (e) => this.setVolume(e.target.value));

        // Fullscreen button
        const fullscreenBtn = this.container.querySelector('#fullscreenBtn');
        fullscreenBtn.addEventListener('click', () => this.toggleFullscreen());

        // Quality selector
        const qualitySelector = this.container.querySelector('#qualitySelector');
        qualitySelector.addEventListener('change', (e) => this.changeQuality(e.target.value));

        // Like button
        const likeBtn = this.container.querySelector('#likeBtn');
        likeBtn.addEventListener('click', () => this.toggleLike());
    }

    setupControls() {
        // Initialize control states
        this.updateVolumeIcon();
        this.hideControls();
    }

    onVideoLoaded() {
        const duration = this.video.duration;
        this.container.querySelector('#totalTime').textContent = this.formatTime(duration);
        this.updateProgress();
    }

    onTimeUpdate() {
        this.updateProgress();
        this.updateHotspots();
        this.updateHotspotsVisibility();
        this.trackAnalytics();
    }

    onPlay() {
        const playIcon = this.container.querySelector('#playIcon');
        const pauseIcon = this.container.querySelector('#pauseIcon');
        playIcon.classList.add('d-none');
        pauseIcon.classList.remove('d-none');

        if (this.analytics.startTime === null) {
            this.analytics.startTime = Date.now();
        }
    }

    onPause() {
        const playIcon = this.container.querySelector('#playIcon');
        const pauseIcon = this.container.querySelector('#pauseIcon');
        playIcon.classList.remove('d-none');
        pauseIcon.classList.add('d-none');

        this.analytics.pauseCount++;
    }

    onVideoEnded() {
        this.onPause();
        if (this.options.enableAnalytics) {
            this.sendAnalytics({ completed: true, watchPercentage: 100 });
        }
    }

    onVolumeChange() {
        this.updateVolumeIcon();
        this.analytics.volumeChangeCount++;
    }

    updateProgress() {
        const video = this.video;
        const progress = (video.currentTime / video.duration) * 100;
        
        this.container.querySelector('#progressFilled').style.width = progress + '%';
        this.container.querySelector('#progressHandle').style.left = progress + '%';
        this.container.querySelector('#currentTime').textContent = this.formatTime(video.currentTime);
    }

    updateHotspots() {
        const currentTime = this.video.currentTime;
        const hotspotsContainer = this.container.querySelector('#videoHotspots');

        // Clear existing hotspots
        hotspotsContainer.innerHTML = '';

        // Show hotspots that should be visible at current time
        this.hotspots.forEach(hotspot => {
            if (currentTime >= hotspot.startTime && currentTime <= hotspot.endTime && hotspot.isActive) {
                this.createHotspotElement(hotspot);
            }
        });
    }

    createHotspotElement(hotspot) {
        const hotspotsContainer = this.container.querySelector('#videoHotspots');
        
        const hotspotEl = document.createElement('div');
        hotspotEl.className = 'video-hotspot';
        hotspotEl.style.left = hotspot.xPosition + '%';
        hotspotEl.style.top = hotspot.yPosition + '%';
        hotspotEl.style.width = hotspot.width + '%';
        hotspotEl.style.height = hotspot.height + '%';

        const buttonClass = `hotspot-button ${hotspot.buttonStyle.toLowerCase()}`;
        hotspotEl.innerHTML = `
            <button class="${buttonClass}" data-hotspot-id="${hotspot.id}">
                ${hotspot.buttonText}
            </button>
        `;

        hotspotsContainer.appendChild(hotspotEl);
    }

    async handleHotspotClick(button) {
        const hotspotId = button.dataset.hotspotId;
        const hotspot = this.hotspots.find(h => h.id == hotspotId);
        
        if (!hotspot) return;

        // Record click analytics
        try {
            await fetch(`${this.options.apiBaseUrl}/video-hotspots/${hotspotId}/click`, {
                method: 'POST'
            });
        } catch (error) {
            console.error('Error recording hotspot click:', error);
        }

        // Handle hotspot action
        switch (hotspot.hotspotType) {
            case 'PRODUCT_LINK':
                if (hotspot.targetProductId) {
                    window.open(`/product-detail.html?id=${hotspot.targetProductId}`, '_blank');
                } else if (hotspot.actionUrl) {
                    window.open(hotspot.actionUrl, '_blank');
                }
                break;
                
            case 'ADD_TO_CART':
                if (hotspot.targetProductId) {
                    this.addToCart(hotspot.targetProductId);
                }
                break;
                
            case 'INFO_POPUP':
                this.showHotspotPopup(hotspot);
                break;
                
            case 'EXTERNAL_LINK':
                if (hotspot.actionUrl) {
                    window.open(hotspot.actionUrl, '_blank');
                }
                break;
        }

        // Track conversion
        if (this.options.enableAnalytics) {
            this.sendAnalytics({ 
                conversionAction: 'HOTSPOT_CLICK',
                conversionValue: hotspot.targetProductId ? 1 : 0 
            });
        }
    }

    showHotspotPopup(hotspot) {
        const modal = new bootstrap.Modal(document.getElementById('hotspotModal'));
        const modalTitle = document.getElementById('hotspotModalTitle');
        const modalBody = document.getElementById('hotspotModalBody');
        const actionBtn = document.getElementById('hotspotActionBtn');

        modalTitle.textContent = hotspot.hotspotName;
        modalBody.innerHTML = hotspot.popupContent;
        
        if (hotspot.targetProductId) {
            actionBtn.onclick = () => {
                window.location.href = `/product-detail.html?id=${hotspot.targetProductId}`;
            };
        }

        modal.show();
    }

    async addToCart(productId) {
        try {
            // Assuming you have an addToCart function
            if (window.addToCart) {
                await window.addToCart(productId);
                this.showNotification('Đã thêm vào giỏ hàng!', 'success');
            }
        } catch (error) {
            console.error('Error adding to cart:', error);
            this.showNotification('Không thể thêm vào giỏ hàng', 'error');
        }
    }

    // Control methods
    togglePlayPause() {
        if (this.video.paused) {
            this.video.play();
        } else {
            this.video.pause();
        }
    }

    seekTo(event) {
        const progressBar = event.currentTarget;
        const rect = progressBar.getBoundingClientRect();
        const percent = (event.clientX - rect.left) / rect.width;
        this.video.currentTime = percent * this.video.duration;
        this.analytics.seekCount++;
    }

    toggleMute() {
        this.video.muted = !this.video.muted;
        this.updateVolumeIcon();
    }

    setVolume(value) {
        this.video.volume = value;
        this.video.muted = value == 0;
        this.updateVolumeIcon();
    }

    updateVolumeIcon() {
        const volumeIcon = this.container.querySelector('#volumeIcon');
        const video = this.video;
        
        if (video.muted || video.volume === 0) {
            volumeIcon.className = 'fas fa-volume-mute';
        } else if (video.volume < 0.5) {
            volumeIcon.className = 'fas fa-volume-down';
        } else {
            volumeIcon.className = 'fas fa-volume-up';
        }
    }

    toggleFullscreen() {
        if (document.fullscreenElement) {
            document.exitFullscreen();
        } else {
            this.container.requestFullscreen();
            this.analytics.fullscreenCount++;
        }
    }

    changeQuality(quality) {
        // This would require multiple video sources
        console.log('Changing quality to:', quality);
        this.analytics.qualityChangeCount++;
    }

    async toggleLike() {
        if (!this.currentVideo) return;

        try {
            const isLiked = this.container.querySelector('#likeBtn i').classList.contains('fas');
            const endpoint = isLiked ? 'unlike' : 'like';
            
            await fetch(`${this.options.apiBaseUrl}/videos/${this.currentVideo.id}/${endpoint}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            // Update UI
            const icon = this.container.querySelector('#likeBtn i');
            const count = this.container.querySelector('#likeCount');
            
            if (isLiked) {
                icon.classList.remove('fas');
                icon.classList.add('far');
                count.textContent = parseInt(count.textContent) - 1;
            } else {
                icon.classList.remove('far');
                icon.classList.add('fas');
                count.textContent = parseInt(count.textContent) + 1;
            }

        } catch (error) {
            console.error('Error toggling like:', error);
        }
    }

    // Analytics methods
    startAnalyticsTracking() {
        this.analytics.startTime = Date.now();
        this.analytics.sessionId = this.generateSessionId();
        
        // Send initial analytics
        this.sendAnalytics({
            deviceType: this.getDeviceType(),
            browserType: this.getBrowserType(),
            referrerUrl: document.referrer,
            userAgent: navigator.userAgent
        });
    }

    trackAnalytics() {
        const currentTime = this.video.currentTime;
        this.analytics.watchDuration = Math.max(this.analytics.watchDuration, currentTime);
        this.analytics.lastPosition = currentTime;
    }

    async sendAnalytics(data = {}) {
        if (!this.options.enableAnalytics || !this.currentVideo) return;

        const analyticsData = {
            videoId: this.currentVideo.id,
            sessionId: this.analytics.sessionId,
            watchDuration: this.analytics.watchDuration,
            watchPercentage: (this.analytics.watchDuration / this.video.duration) * 100,
            pausedCount: this.analytics.pauseCount,
            seekedCount: this.analytics.seekCount,
            volumeChangedCount: this.analytics.volumeChangeCount,
            fullscreenCount: this.analytics.fullscreenCount,
            exitTime: this.analytics.lastPosition,
            ...data
        };

        try {
            await fetch(`${this.options.apiBaseUrl}/videos/analytics`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(analyticsData)
            });
        } catch (error) {
            console.error('Error sending analytics:', error);
        }
    }

    // Utility methods
    showControls() {
        this.container.querySelector('.video-wrapper').classList.add('show-controls');
        clearTimeout(this.controlsTimeout);
        this.controlsTimeout = setTimeout(() => this.hideControls(), 3000);
    }

    hideControls() {
        if (!this.video.paused) {
            this.container.querySelector('.video-wrapper').classList.remove('show-controls');
        }
    }

    showLoading(show) {
        const loading = this.container.querySelector('#videoLoading');
        loading.classList.toggle('d-none', !show);
    }

    showError() {
        this.showLoading(false);
        this.container.querySelector('#videoError').classList.remove('d-none');
    }

    updateVideoInfo() {
        if (!this.currentVideo) return;

        this.container.querySelector('#videoTitle').textContent = this.currentVideo.title;
        this.container.querySelector('#videoViews').textContent = `${this.formatNumber(this.currentVideo.viewCount)} lượt xem`;
        this.container.querySelector('#videoDuration').textContent = this.currentVideo.formattedDuration;
        this.container.querySelector('#videoCategory').textContent = this.currentVideo.category;
        this.container.querySelector('#likeCount').textContent = this.formatNumber(this.currentVideo.likeCount);
    }

    detectDevice() {
        const width = window.innerWidth;
        if (width < 768) {
            this.analytics.deviceType = 'MOBILE';
        } else if (width < 1024) {
            this.analytics.deviceType = 'TABLET';
        } else {
            this.analytics.deviceType = 'DESKTOP';
        }
    }

    handleKeyboard(event) {
        if (!this.container.contains(document.activeElement)) return;

        switch (event.code) {
            case 'Space':
                event.preventDefault();
                this.togglePlayPause();
                break;
            case 'ArrowLeft':
                event.preventDefault();
                this.video.currentTime -= 10;
                break;
            case 'ArrowRight':
                event.preventDefault();
                this.video.currentTime += 10;
                break;
            case 'KeyM':
                event.preventDefault();
                this.toggleMute();
                break;
            case 'KeyF':
                event.preventDefault();
                this.toggleFullscreen();
                break;
        }
    }

    formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }

    formatNumber(num) {
        if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
        if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
        return num.toString();
    }

    generateSessionId() {
        return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }

    getDeviceType() {
        return this.analytics.deviceType || 'DESKTOP';
    }

    getBrowserType() {
        const ua = navigator.userAgent;
        if (ua.includes('Chrome')) return 'Chrome';
        if (ua.includes('Firefox')) return 'Firefox';
        if (ua.includes('Safari')) return 'Safari';
        if (ua.includes('Edge')) return 'Edge';
        return 'Other';
    }

    showNotification(message, type = 'info') {
        // Create and show notification
        const notification = document.createElement('div');
        notification.className = `alert alert-${type} position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    // Public API methods
    play() {
        return this.video.play();
    }

    pause() {
        this.video.pause();
    }

    getCurrentTime() {
        return this.video.currentTime;
    }

    setCurrentTime(time) {
        this.video.currentTime = time;
    }

    getDuration() {
        return this.video.duration;
    }

    destroy() {
        // Send final analytics
        if (this.options.enableAnalytics) {
            this.sendAnalytics({ 
                exitTime: this.video.currentTime,
                watchPercentage: (this.analytics.watchDuration / this.video.duration) * 100
            });
        }

        // Clean up event listeners
        this.video.removeEventListener('loadedmetadata', this.onVideoLoaded);
        this.video.removeEventListener('timeupdate', this.onTimeUpdate);
        // ... remove other listeners
    }
}

// Global function to retry video loading
function retryVideo() {
    const videoError = document.getElementById('videoError');
    videoError.classList.add('d-none');
    
    if (window.currentVideoPlayer) {
        window.currentVideoPlayer.loadVideo(window.currentVideoPlayer.currentVideo?.id);
    }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ProductVideoPlayer;
} else {
    window.ProductVideoPlayer = ProductVideoPlayer;
}
