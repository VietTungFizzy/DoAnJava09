-- Create enhanced videos table for e-commerce product demos
CREATE TABLE IF NOT EXISTS videos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    video_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    duration INT, -- in seconds
    file_size BIGINT, -- in bytes
    video_format VARCHAR(50),
    resolution VARCHAR(20),
    category VARCHAR(100),
    tags TEXT, -- comma-separated tags
    view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, PROCESSING, DELETED
    is_featured BOOLEAN DEFAULT FALSE,
    is_public BOOLEAN DEFAULT TRUE,
    
    -- Product-specific video features
    video_type VARCHAR(50) DEFAULT 'GENERAL', -- PRODUCT_DEMO, TUTORIAL, TESTIMONIAL, UNBOXING, INSTALLATION
    auto_play BOOLEAN DEFAULT FALSE,
    muted_by_default BOOLEAN DEFAULT TRUE,
    show_controls BOOLEAN DEFAULT TRUE,
    enable_fullscreen BOOLEAN DEFAULT TRUE,
    enable_subtitles BOOLEAN DEFAULT FALSE,
    subtitle_url VARCHAR(500),
    poster_image_url VARCHAR(500),
    video_quality JSON, -- JSON array of available qualities
    
    -- Interactive features
    has_hotspots BOOLEAN DEFAULT FALSE,
    hotspots_data JSON, -- JSON data for interactive hotspots
    
    -- Analytics fields
    completion_rate DECIMAL(5,2) DEFAULT 0.00,
    average_watch_time DECIMAL(8,2) DEFAULT 0.00, -- in seconds
    engagement_score DECIMAL(5,2) DEFAULT 0.00,
    conversion_count BIGINT DEFAULT 0, -- clicks to product/add to cart
    
    uploaded_by INT NOT NULL,
    product_id INT, -- Related product for product demo videos
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    INDEX idx_video_status (status),
    INDEX idx_video_category (category),
    INDEX idx_video_type (video_type),
    INDEX idx_video_featured (is_featured),
    INDEX idx_video_public (is_public),
    INDEX idx_video_uploaded_by (uploaded_by),
    INDEX idx_video_product_id (product_id),
    INDEX idx_video_created_at (created_at),
    INDEX idx_video_view_count (view_count),
    INDEX idx_video_like_count (like_count),
    INDEX idx_video_completion_rate (completion_rate),
    INDEX idx_video_engagement_score (engagement_score),
    FULLTEXT INDEX idx_video_search (title, description, tags)
);

-- Create video hotspots table for interactive features
CREATE TABLE IF NOT EXISTS video_hotspots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    video_id INT NOT NULL,
    hotspot_name VARCHAR(255) NOT NULL,
    start_time DECIMAL(8,2) NOT NULL, -- in seconds
    end_time DECIMAL(8,2) NOT NULL, -- in seconds
    x_position DECIMAL(5,2) NOT NULL, -- percentage from left (0-100)
    y_position DECIMAL(5,2) NOT NULL, -- percentage from top (0-100)
    width DECIMAL(5,2) DEFAULT 10.00, -- percentage width (0-100)
    height DECIMAL(5,2) DEFAULT 10.00, -- percentage height (0-100)
    hotspot_type VARCHAR(50) DEFAULT 'PRODUCT_LINK', -- PRODUCT_LINK, ADD_TO_CART, INFO_POPUP, EXTERNAL_LINK
    action_url VARCHAR(500),
    popup_content TEXT,
    button_text VARCHAR(100) DEFAULT 'Xem chi tiết',
    button_style VARCHAR(20) DEFAULT 'PRIMARY', -- PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER
    is_active BOOLEAN DEFAULT TRUE,
    click_count BIGINT DEFAULT 0,
    target_product_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
    FOREIGN KEY (target_product_id) REFERENCES products(id) ON DELETE SET NULL,
    INDEX idx_hotspot_video_id (video_id),
    INDEX idx_hotspot_time_range (start_time, end_time),
    INDEX idx_hotspot_active (is_active),
    INDEX idx_hotspot_target_product (target_product_id),
    INDEX idx_hotspot_click_count (click_count)
);

-- Create video analytics table for tracking user interactions
CREATE TABLE IF NOT EXISTS video_analytics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    video_id INT NOT NULL,
    user_id INT, -- NULL for anonymous users
    session_id VARCHAR(100),
    user_agent TEXT,
    ip_address VARCHAR(45),
    watch_duration DECIMAL(8,2) DEFAULT 0.00, -- actual time watched in seconds
    watch_percentage DECIMAL(5,2) DEFAULT 0.00, -- percentage of video watched
    completed BOOLEAN DEFAULT FALSE,
    paused_count INT DEFAULT 0,
    seeked_count INT DEFAULT 0,
    volume_changed_count INT DEFAULT 0,
    fullscreen_count INT DEFAULT 0,
    quality_changed_count INT DEFAULT 0,
    device_type VARCHAR(20), -- MOBILE, TABLET, DESKTOP
    browser_type VARCHAR(50),
    referrer_url TEXT,
    exit_time DECIMAL(8,2), -- time when user left the video
    conversion_action VARCHAR(50), -- PRODUCT_VIEW, ADD_TO_CART, PURCHASE, HOTSPOT_CLICK
    conversion_value DECIMAL(10,2), -- monetary value if applicable
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_analytics_video_id (video_id),
    INDEX idx_analytics_user_id (user_id),
    INDEX idx_analytics_session_id (session_id),
    INDEX idx_analytics_device_type (device_type),
    INDEX idx_analytics_conversion (conversion_action),
    INDEX idx_analytics_created_at (created_at)
);

-- Insert sample data
INSERT INTO videos (title, description, video_url, thumbnail_url, duration, file_size, video_format, resolution, category, tags, view_count, like_count, is_featured, is_public, uploaded_by) VALUES
('Introduction to Java Programming', 'Learn the basics of Java programming language', 'https://example.com/videos/java-intro.mp4', 'https://example.com/thumbnails/java-intro.jpg', 1800, 157286400, 'mp4', '1080p', 'Education', 'java,programming,tutorial', 1250, 89, TRUE, TRUE, 1),
('Spring Boot Tutorial', 'Complete guide to Spring Boot framework', 'https://example.com/videos/spring-boot.mp4', 'https://example.com/thumbnails/spring-boot.jpg', 2400, 209715200, 'mp4', '1080p', 'Education', 'spring,boot,java,framework', 2100, 156, TRUE, TRUE, 1),
('React.js Fundamentals', 'Learn React.js from scratch', 'https://example.com/videos/react-fundamentals.mp4', 'https://example.com/thumbnails/react-fundamentals.jpg', 3000, 262144000, 'mp4', '1080p', 'Education', 'react,javascript,frontend', 1800, 134, FALSE, TRUE, 1),
('Database Design Principles', 'Learn how to design efficient databases', 'https://example.com/videos/database-design.mp4', 'https://example.com/thumbnails/database-design.jpg', 2700, 235929600, 'mp4', '720p', 'Education', 'database,sql,design', 950, 67, FALSE, TRUE, 1),
('Product Demo Video', 'Demonstration of our latest product features', 'https://example.com/videos/product-demo.mp4', 'https://example.com/thumbnails/product-demo.jpg', 600, 52428800, 'mp4', '1080p', 'Business', 'product,demo,features', 3200, 245, TRUE, TRUE, 1);
