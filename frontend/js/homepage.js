// Homepage Product Loading
const HomepageProducts = {
    apiBaseUrl: 'http://localhost:8080/api',
    categories: [],
    
    // Fetch all categories
    async fetchCategories() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/categories`);
            if (!response.ok) {
                console.error('Failed to load categories');
                return [];
            }
            
            const data = await response.json();
            this.categories = data || [];
            return this.categories;
        } catch (error) {
            console.error('Error loading categories:', error);
            return [];
        }
    },
    
    // Render category tabs
    renderCategoryTabs() {
        const tabsContainer = document.getElementById('trendTabs');
        if (!tabsContainer) {
            console.error('Tabs container not found');
            return;
        }
        
        // Clear existing content
        tabsContainer.innerHTML = '';
        
        if (this.categories.length === 0) {
            tabsContainer.innerHTML = `
                <li class="nav-item">
                    <span class="nav-link disabled">No categories available</span>
                </li>
            `;
            return;
        }
        
        // Create tab for each category
        this.categories.forEach((category, index) => {
            const tabId = `category-${category.id}`;
            const isActive = index === 0 ? 'active' : '';
            
            const tabItem = document.createElement('li');
            tabItem.className = 'nav-item';
            tabItem.innerHTML = `
                <a class="nav-link ${isActive}" 
                   data-bs-toggle="tab" 
                   href="#${tabId}"
                   data-category-id="${category.id}"
                   data-category-name="${category.name}">
                    ${category.name}
                </a>
            `;
            
            tabsContainer.appendChild(tabItem);
        });
    },
    
    // Render category tab content containers
    renderCategoryTabContent() {
        const contentContainer = document.getElementById('trendTabContent');
        if (!contentContainer) {
            console.error('Tab content container not found');
            return;
        }
        
        // Clear existing content
        contentContainer.innerHTML = '';
        
        if (this.categories.length === 0) {
            contentContainer.innerHTML = `
                <div class="text-center py-5">
                    <p class="text-muted">No categories available</p>
                </div>
            `;
            return;
        }
        
        // Create tab pane for each category
        this.categories.forEach((category, index) => {
            const tabId = `category-${category.id}`;
            const isActive = index === 0 ? 'show active' : '';
            
            const tabPane = document.createElement('div');
            tabPane.className = `tab-pane fade ${isActive}`;
            tabPane.id = tabId;
            tabPane.innerHTML = `
                <div class="row row-cols-1 row-cols-md-4 g-4" id="${tabId}-products">
                    <div class="col-12 text-center py-5">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
            `;
            
            contentContainer.appendChild(tabPane);
        });
    },
    
    // Setup tab change event listeners
    setupTabListeners() {
        const tabsContainer = document.getElementById('trendTabs');
        if (!tabsContainer) return;
        
        // Listen for tab changes
        tabsContainer.addEventListener('click', async (e) => {
            const tabLink = e.target.closest('[data-bs-toggle="tab"]');
            if (!tabLink) return;
            
            const categoryId = tabLink.getAttribute('data-category-id');
            const categoryName = tabLink.getAttribute('data-category-name');
            const tabId = tabLink.getAttribute('href').substring(1); // Remove #
            
            if (categoryId && categoryName) {
                // Load products for this category
                await this.renderProductsInTab(`${tabId}-products`, categoryId, 8);
            }
        });
    },
    
    // Load products for homepage
    async loadProducts(category = 'all', limit = 8) {
        try {
            let url = `${this.apiBaseUrl}/products?page=0&size=${limit}`;
            
            // Add category filter if needed
            if (category !== 'all') {
                url += `&categoryIds=${encodeURIComponent(category)}`;
            }
            
            const headers = {};
            const token = localStorage.getItem('token');
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
            
            const response = await fetch(url, { headers });
            if (!response.ok)
                 {
                console.error('Failed to load products');
                return [];
            }
            
            const data = await response.json();
            return data.content || []; // Return array of products
        } catch (error) {
            console.error('Error loading products:', error);
            return [];
        }
    },
    
    // Render product card HTML
    renderProductCard(product) {
        const imageUrl = product.imageUrl || 'https://via.placeholder.com/120';
        const price = product.price || 'N/A';
        const name = product.name || 'Product';
        
        // Determine wishlist button state
        const isInWishlist = product.inWishlist || false;
        const heartClass = isInWishlist ? 'fa-solid fa-heart' : 'fa-regular fa-heart';
        
        return `
            <div class="col">
                <div class="product-card">
                    <button 
                        class="fav-btn wishlist-toggle text-danger" 
                        data-product-id="${product.id}" 
                        title="Add to Wishlist" 
                        data-in-wishlist="${isInWishlist}"
                    >
                        <i class="${heartClass}"></i>
                    </button>
                    <img src="${imageUrl}" alt="${name}" onerror="this.src='https://via.placeholder.com/120'">
                    <div class="fw-bold mb-1">${name}</div>
                    <div class="text-muted" style="font-size:0.9rem;">${product.description || 'Product description'}</div>
                    <div class="fw-bold mt-2" style="color:#0070f3;">$${price}</div>
                </div>
            </div>
        `;
    },
    
    // Render products in a tab
    async renderProductsInTab(containerId, category = 'all', limit = 4) {
        const products = await this.loadProducts(category, limit);
        const rowElement = document.getElementById(containerId);
        
        if (!rowElement) {
            console.error(`Container ${containerId} not found`);
            return;
        }
        
        // Clear existing content
        rowElement.innerHTML = '';
        
        // Render products
        if (products.length === 0) {
            rowElement.innerHTML = `
                <div class="col-12 text-center py-5">
                    <p class="text-muted">No products found in this category.</p>
                </div>
            `;
        } else {
            products.forEach(product => {
                rowElement.innerHTML += this.renderProductCard(product);
            });
            
            // Add event listeners for product cards
            const productCards = rowElement.querySelectorAll('.product-card');
            productCards.forEach(card => {
                card.addEventListener('click', () => {
                    const productId = card.querySelector('.wishlist-toggle').getAttribute('data-product-id');
                    window.location.href = `product-detail.html?id=${productId}`;
                });
            });
            
            // Prevent card click when clicking wishlist button
            const wishlistButtons = rowElement.querySelectorAll('.wishlist-toggle');
            wishlistButtons.forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    if (window.wishlistManager) {
                        const productId = btn.getAttribute('data-product-id');
                        window.wishlistManager.toggleWishlistItem(productId, btn);
                    }
                });
            });
        }
    },
    
    // Initialize homepage products
    async init() {
        // Fetch categories from API
        await this.fetchCategories();
        
        // Render category tabs
        this.renderCategoryTabs();
        
        // Render tab content containers
        this.renderCategoryTabContent();
        
        // Setup tab change listeners
        this.setupTabListeners();
        
        // Load products for the first category (if exists)
        if (this.categories.length > 0) {
            const firstCategory = this.categories[0];
            const tabId = `category-${firstCategory.id}`;
            await this.renderProductsInTab(`${tabId}-products`, firstCategory.id, 8);
        }
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Messages container for wishlist notifications
    $('body').prepend('<div class="messages-container position-fixed top-0 end-0 p-3" style="z-index: 9999;"></div>');

    // Initialize wishlist manager first
    if (typeof WishlistManager !== 'undefined') {
        wishlistManager = new WishlistManager();
    }
    
    // Load homepage products
    HomepageProducts.init();

    // Initialize video player
    window.homepageVideoPlayer = new HomepageVideoPlayer();

    // Update user account link
    updateUserAccountLink();
    
    // Load wishlist statuses if logged in
    const token = localStorage.getItem('token');
    if (token) {
        loadWishlistStatuses();
    }
});

// Function to scroll to products section
function scrollToProducts() {
    const productsSection = document.querySelector('.trending-tabs');
    if (productsSection) {
        productsSection.scrollIntoView({ behavior: 'smooth' });
    }
}

// Load wishlist status for all products on the page
async function loadWishlistStatuses() {
    const token = localStorage.getItem('token');
    if (!token) return;
    
    // Get all wishlist toggle buttons
    const wishlistButtons = document.querySelectorAll('.wishlist-toggle');
    
    // Check each product
    wishlistButtons.forEach(async (button) => {
        const productId = button.getAttribute('data-product-id');
        if (productId && wishlistManager) {
            const isInWishlist = await wishlistManager.isProductInWishlist(productId);
            updateWishlistButtonState(button, isInWishlist);
        }
    });
}

// Update wishlist button visual state
function updateWishlistButtonState(button, isInWishlist) {
    const icon = button.querySelector('i');
    if (icon) {
        if (isInWishlist) {
            icon.classList.remove('far');
            icon.classList.add('fas', 'text-danger');
        } else {
            icon.classList.remove('fas', 'text-danger');
            icon.classList.add('far');
        }
    }
}

// Homepage Video Player
class HomepageVideoPlayer {
    // Class constants for better maintainability
    static CONTROLS_TIMEOUT = 3000; // 3 seconds
    static DEFAULT_VOLUME = 0.5;
    
    constructor() {
        this.video = document.getElementById('homepageVideo');
        this.controls = document.getElementById('homepageVideoControls');
        this.playPauseBtn = document.getElementById('homepagePlayPauseBtn');
        this.playIcon = document.getElementById('homepagePlayIcon');
        this.pauseIcon = document.getElementById('homepagePauseIcon');
        this.muteBtn = document.getElementById('homepageMuteBtn');
        this.volumeIcon = document.getElementById('homepageVolumeIcon');
        this.volumeSlider = document.getElementById('homepageVolumeSlider');
        this.fullscreenBtn = document.getElementById('homepageFullscreenBtn');
        this.progressFilled = document.getElementById('homepageProgressFilled');
        this.progressHandle = document.getElementById('homepageProgressHandle');
        this.currentTimeEl = document.getElementById('homepageCurrentTime');
        this.totalTimeEl = document.getElementById('homepageTotalTime');
        this.progressBar = document.querySelector('#homepageVideoControls .progress-bar');
        
        this.init();
    }

    init() {
        this.setupVideoSource();
        this.setupEventListeners();
        this.setupAutoplay();
    }

    setupVideoSource() {
        // Set video source and poster from data attributes
        const videoSrc = this.video.dataset.videoSrc;
        const posterSrc = this.video.dataset.poster;
        
        if (videoSrc) {
            this.video.src = videoSrc;
        }
        
        if (posterSrc) {
            this.video.poster = posterSrc;
        }
    }

    setupEventListeners() {
        // Video events
        this.video.addEventListener('loadedmetadata', () => this.onVideoLoaded());
        this.video.addEventListener('timeupdate', () => this.onTimeUpdate());
        this.video.addEventListener('play', () => this.onPlay());
        this.video.addEventListener('pause', () => this.onPause());
        this.video.addEventListener('ended', () => this.onVideoEnded());
        this.video.addEventListener('volumechange', () => this.onVolumeChange());

        // Control events
        this.playPauseBtn.addEventListener('click', () => this.togglePlayPause());
        this.muteBtn.addEventListener('click', () => this.toggleMute());
        this.volumeSlider.addEventListener('input', (e) => this.setVolume(e.target.value));
        this.fullscreenBtn.addEventListener('click', () => this.toggleFullscreen());
        this.progressBar.addEventListener('click', (e) => this.seekTo(e));

        // Mouse events for showing/hiding controls
        const videoWrapper = this.video.closest('.video-wrapper');
        videoWrapper.addEventListener('mouseenter', () => this.showControls());
        videoWrapper.addEventListener('mouseleave', () => this.hideControls());
        videoWrapper.addEventListener('mousemove', () => this.showControls());

        // Click on overlay to unmute and show controls
        const overlay = document.querySelector('.video-overlay');
        overlay.addEventListener('click', () => this.unmuteAndShowControls());
    }

    setupAutoplay() {
        // Ensure video is muted for autoplay (browser requirement)
        this.video.muted = true;
        this.volumeSlider.value = 0;
        this.updateVolumeIcon();

        // Try to autoplay
        this.video.play().catch(e => {
            console.log('Autoplay failed:', e);
            // If autoplay fails, show play button
            this.showPlayButton();
        });
    }

    onVideoLoaded() {
        const duration = this.video.duration;
        this.totalTimeEl.textContent = this.formatTime(duration);
        this.updateProgress();
    }

    onTimeUpdate() {
        this.updateProgress();
    }

    onPlay() {
        this.playIcon.classList.add('d-none');
        this.pauseIcon.classList.remove('d-none');
        this.hideOverlay();
    }

    onPause() {
        this.playIcon.classList.remove('d-none');
        this.pauseIcon.classList.add('d-none');
    }

    onVideoEnded() {
        this.onPause();
        // Restart video for loop
        this.video.currentTime = 0;
        this.video.play();
    }

    onVolumeChange() {
        this.updateVolumeIcon();
    }

    updateProgress() {
        const progress = (this.video.currentTime / this.video.duration) * 100;
        this.progressFilled.style.width = progress + '%';
        this.progressHandle.style.left = progress + '%';
        this.currentTimeEl.textContent = this.formatTime(this.video.currentTime);
    }

    updateVolumeIcon() {
        if (this.video.muted || this.video.volume === 0) {
            this.volumeIcon.className = 'fas fa-volume-mute';
        } else if (this.video.volume < 0.5) {
            this.volumeIcon.className = 'fas fa-volume-down';
        } else {
            this.volumeIcon.className = 'fas fa-volume-up';
        }
    }

    togglePlayPause() {
        if (this.video.paused) {
            this.video.play();
        } else {
            this.video.pause();
        }
    }

    toggleMute() {
        this.video.muted = !this.video.muted;
        this.volumeSlider.value = this.video.muted ? 0 : this.video.volume;
        this.updateVolumeIcon();
    }

    setVolume(value) {
        this.video.volume = value;
        this.video.muted = value === 0;
        this.updateVolumeIcon();
    }

    toggleFullscreen() {
        const container = this.video.closest('.video-player-container');
        if (document.fullscreenElement) {
            document.exitFullscreen();
        } else {
            container.requestFullscreen();
        }
    }

    seekTo(event) {
        const rect = this.progressBar.getBoundingClientRect();
        const percent = (event.clientX - rect.left) / rect.width;
        this.video.currentTime = percent * this.video.duration;
    }

    showControls() {
        this.controls.style.opacity = '1';
        clearTimeout(this.controlsTimeout);
        this.controlsTimeout = setTimeout(() => this.hideControls(), HomepageVideoPlayer.CONTROLS_TIMEOUT);
    }

    hideControls() {
        if (!this.video.paused) {
            this.controls.style.opacity = '0';
        }
    }

    showPlayButton() {
        // Show play button if autoplay fails
        const overlay = document.querySelector('.video-overlay');
        overlay.style.display = 'flex';
    }

    hideOverlay() {
        // Hide overlay when video starts playing
        const overlay = document.querySelector('.video-overlay');
        overlay.style.display = 'none';
    }

    unmuteAndShowControls() {
        // Unmute video and show controls when user interacts
        this.video.muted = false;
        this.volumeSlider.value = HomepageVideoPlayer.DEFAULT_VOLUME;
        this.video.volume = HomepageVideoPlayer.DEFAULT_VOLUME;
        this.updateVolumeIcon();
        this.showControls();
        this.hideOverlay();
    }

    formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
}

// Make it globally available
window.HomepageProducts = HomepageProducts;

