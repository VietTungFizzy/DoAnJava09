// Product Detail Page JavaScript
$(document).ready(function() {
    // Get product ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    if (productId) {
        fetchProductDetails(productId);
    } else {
        console.error('No product ID found in URL');
        // Maybe redirect to homepage or show error
    }
});

// Fetch product details from backend
async function fetchProductDetails(productId) {
    try {
        const token = localStorage.getItem('token');
        const headers = token ? { 'Authorization': `Bearer ${token}` } : {};
        const response = await fetch(AppConfig.getApiUrl(AppConfig.api.endpoints.products + '/' + productId), { headers });
        if (!response.ok) {
            throw new Error('Failed to fetch product details');
        }
        const product = await response.json();
        
        // Fetch related products
        const relatedResponse = await fetch(AppConfig.getApiUrl(AppConfig.api.endpoints.products + '/' + productId + '/related'), { headers });
        let relatedProducts = [];
        if (relatedResponse.ok) {
            relatedProducts = await relatedResponse.json();
        } else {
            console.warn('Failed to fetch related products');
        }
        
        populateProductDetails(product);
        populateSimilarProducts(relatedProducts);
    } catch (error) {
        console.error('Error fetching product details:', error);
        // Show error message to user
        showErrorMessage('Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.');
    }
}

// Populate product details into the DOM
function populateProductDetails(product) {
    // Update breadcrumb
    $('.breadcrumb-item.active').text(product.name);

    // Update main image
    $('#mainProductImage').attr('src', product.imageUrl || 'placeholder.jpg');

    // Update thumbnails
    const thumbnailsContainer = $('.product-thumbnails');
    thumbnailsContainer.empty();
    if (product.images && product.images.length > 0) {
        product.images.forEach((image, index) => {
            const img = $('<img>').attr('src', image).attr('alt', `thumb${index + 1}`);
            if (index === 0) img.addClass('selected');
            thumbnailsContainer.append(img);
        });
    }

    // Update product info
    $('.product-title').text(product.name);
    $('.product-price').text(formatPrice(product.price));
    $('.product-sale').text(product.shippingInfo || 'Miễn phí vận chuyển toàn quốc');

    // Update rating
    updateRating(product.rating, product.reviewCount, product.soldCount);

    // Update description tab
    $('#desc').html('<strong>Mô tả sản phẩm</strong><br/>' + (product.description || '<p>Đang cập nhật...</p>'));

    // Update specifications
    $('#spec').html(product.specifications || 'Thông số kỹ thuật đang cập nhật...');

    // Update usage instructions
    $('#usage').html(product.usageInstructions || 'Hướng dẫn sử dụng đang cập nhật...');

    // Update reviews
    $('#review').html(product.reviews || 'Đánh giá sản phẩm đang cập nhật...');

    // Set product ID for buttons
    $('.btn-add-cart, .btn-buy-now, #wishlist-btn').attr('data-product-id', product.id);

    // Update wishlist button
    updateWishlistButton(product.inWishlist);
}

// Populate similar products
function populateSimilarProducts(relatedProducts) {
    const container = $('.similar-products-container .d-flex.flex-wrap');
    container.empty();
    
    if (relatedProducts && relatedProducts.length > 0) {
        relatedProducts.forEach(product => {
            const productDiv = $('<div>').addClass('similar-product');
            const img = $('<img>').attr('src', product.imageUrl || 'placeholder.jpg').attr('alt', product.name);
            const nameDiv = $('<div>').text(product.name);
            
            // Make it clickable to go to product detail
            productDiv.on('click', function() {
                window.location.href = `product-detail.html?id=${product.id}`;
            });
            
            productDiv.append(img).append(nameDiv);
            container.append(productDiv);
        });
    } else {
        container.append('<div class="similar-product"><div>Không có sản phẩm tương tự</div></div>');
    }
}

// Format price in VND
function formatPrice(price) {
    return '₫ ' + price.toLocaleString('vi-VN');
}

// Update rating display
function updateRating(rating, reviewCount, soldCount) {
    const ratingContainer = $('.product-rating');
    ratingContainer.empty();

    // Stars
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    for (let i = 0; i < fullStars; i++) {
        ratingContainer.append('<i class="fas fa-star"></i>');
    }
    if (hasHalfStar) {
        ratingContainer.append('<i class="fas fa-star-half-alt"></i>');
    }
    const emptyStars = 5 - Math.ceil(rating);
    for (let i = 0; i < emptyStars; i++) {
        ratingContainer.append('<i class="far fa-star"></i>');
    }

    // Rating text
    ratingContainer.append(` ${rating}`);

    // Reviews and sold
    const statsText = `${reviewCount || 0} đánh giá | ${soldCount || 0} đã bán`;
    ratingContainer.next().text(statsText);
}

// Show error message
function showErrorMessage(message) {
    // You can implement a toast or alert here
    alert(message);
}

// Image thumbnail click functionality
$(document).ready(function() {
    // Thumbnail click handler
    $(document).on('click', '.product-thumbnails img', function() {
        $('.product-thumbnails img').removeClass('selected');
        $(this).addClass('selected');
        $('#mainProductImage').attr('src', $(this).attr('src'));
    });
});

// Update wishlist button appearance
function updateWishlistButton(isInWishlist) {
    const $button = $('#wishlist-btn');
    const $text = $('#wishlist-text');
    
    if (isInWishlist) {
        $button.removeClass('btn-outline-danger').addClass('btn-danger');
        $text.text('Đã thêm vào danh sách yêu thích');
    } else {
        $button.removeClass('btn-danger').addClass('btn-outline-danger');
        $text.text('Thêm vào danh sách yêu thích');
    }
}
