// Wishlist functionality
class WishlistManager {
    constructor() {
        this.apiBaseUrl = 'http://localhost:8080/api/wishlist';
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.loadWishlistCount();
    }

    setupEventListeners() {
        // Wishlist toggle buttons
        $(document).on('click', '.wishlist-toggle', (e) => {
            e.preventDefault();
            const productId = $(e.currentTarget).data('product-id');
            this.toggleWishlistItem(productId, e.currentTarget);
        });

        // Wishlist page navigation
        $(document).on('click', '.view-wishlist', (e) => {
            e.preventDefault();
            this.navigateToWishlist();
        });
    }

    // Toggle product in wishlist
    async toggleWishlistItem(productId, buttonElement) {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.showMessage('Please login to use wishlist', 'error');
                this.redirectToLogin();
                return;
            }

            const response = await fetch(`${this.apiBaseUrl}/toggle/${productId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();

            if (response.ok) {
                this.updateWishlistButton(buttonElement, result);
                this.loadWishlistCount();
                this.showMessage(result.message || 'Wishlist updated', 'success');
            } else {
                this.showMessage(result.error || 'Failed to update wishlist', 'error');
            }
        } catch (error) {
            console.error('Wishlist toggle error:', error);
            this.showMessage('Network error. Please try again.', 'error');
        }
    }

    // Add product to wishlist
    async addToWishlist(productId, options = {}) {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.showMessage('Please login to use wishlist', 'error');
                this.redirectToLogin();
                return false;
            }

            const requestData = {
                productId: productId,
                notes: options.notes || '',
                priority: options.priority || 1,
                isNotified: options.isNotified || false
            };

            const response = await fetch(this.apiBaseUrl, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            const result = await response.json();

            if (response.ok) {
                this.showMessage('Product added to wishlist', 'success');
                this.loadWishlistCount();
                return true;
            } else {
                this.showMessage(result.error || 'Failed to add to wishlist', 'error');
                return false;
            }
        } catch (error) {
            console.error('Add to wishlist error:', error);
            this.showMessage('Network error. Please try again.', 'error');
            return false;
        }
    }

    // Remove product from wishlist
    async removeFromWishlist(productId) {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.showMessage('Please login to use wishlist', 'error');
                return false;
            }

            const response = await fetch(`${this.apiBaseUrl}/product/${productId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();

            if (response.ok) {
                this.showMessage('Product removed from wishlist', 'success');
                this.loadWishlistCount();
                return true;
            } else {
                this.showMessage(result.error || 'Failed to remove from wishlist', 'error');
                return false;
            }
        } catch (error) {
            console.error('Remove from wishlist error:', error);
            this.showMessage('Network error. Please try again.', 'error');
            return false;
        }
    }

    // Check if product is in wishlist
    async isProductInWishlist(productId) {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                return false;
            }

            const response = await fetch(`${this.apiBaseUrl}/check/${productId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();
            return result.isInWishlist || false;
        } catch (error) {
            console.error('Check wishlist error:', error);
            return false;
        }
    }

    // Load wishlist count
    async loadWishlistCount() {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.updateWishlistCountDisplay(0);
                return;
            }

            const response = await fetch(`${this.apiBaseUrl}/count`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();
            this.updateWishlistCountDisplay(result.count || 0);
        } catch (error) {
            console.error('Load wishlist count error:', error);
            this.updateWishlistCountDisplay(0);
        }
    }

    // Update wishlist count display
    updateWishlistCountDisplay(count) {
        $('.wishlist-count').text(count);
        $('.wishlist-badge').text(count);
        
        if (count > 0) {
            $('.wishlist-count').addClass('badge bg-danger');
        } else {
            $('.wishlist-count').removeClass('badge bg-danger');
        }
    }

    // Update wishlist button state
    updateWishlistButton(buttonElement, result) {
        const $button = $(buttonElement);
        const $icon = $button.find('i');
        
        if (result.message && result.message.includes('removed')) {
            $icon.removeClass('fas fa-heart').addClass('far fa-heart');
            $button.removeClass('text-danger').addClass('text-muted');
        } else {
            $icon.removeClass('far fa-heart').addClass('fas fa-heart');
            $button.removeClass('text-muted').addClass('text-danger');
        }
    }

    // Load user's wishlist
    async loadWishlist(page = 0, size = 10, sortBy = 'createdAt', sortDir = 'desc') {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.showMessage('Please login to view wishlist', 'error');
                this.redirectToLogin();
                return;
            }

            const response = await fetch(
                `${this.apiBaseUrl}?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`,
                {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            const result = await response.json();

            if (response.ok) {
                this.displayWishlist(result.content || []);
                this.updatePagination(result);
            } else {
                this.showMessage(result.error || 'Failed to load wishlist', 'error');
            }
        } catch (error) {
            console.error('Load wishlist error:', error);
            this.showMessage('Network error. Please try again.', 'error');
        }
    }

    // Display wishlist items
    displayWishlist(items) {
        const $container = $('#wishlist-container');
        $container.empty();

        if (items.length === 0) {
            $container.html(`
                <div class="text-center py-5">
                    <i class="fas fa-heart fa-3x text-muted mb-3"></i>
                    <h5 class="text-muted">Your wishlist is empty</h5>
                    <p class="text-muted">Add products to your wishlist to see them here</p>
                    <a href="homepage.html" class="btn btn-primary">Continue Shopping</a>
                </div>
            `);
            return;
        }

        items.forEach(item => {
            const wishlistItem = this.createWishlistItemHTML(item);
            $container.append(wishlistItem);
        });
    }

    // Create wishlist item HTML
    createWishlistItemHTML(item) {
        const priorityClass = this.getPriorityClass(item.priority);
        const priorityText = this.getPriorityText(item.priority);
        
        return `
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card h-100">
                    <div class="position-relative">
                        <img src="${item.productImageUrl || 'https://via.placeholder.com/300'}" 
                             class="card-img-top" 
                             alt="${item.productName}"
                             style="height: 200px; object-fit: cover;">
                        <button class="btn btn-sm btn-danger position-absolute top-0 end-0 m-2 wishlist-remove" 
                                data-product-id="${item.productId}"
                                title="Remove from wishlist">
                            <i class="fas fa-times"></i>
                        </button>
                        <span class="badge ${priorityClass} position-absolute top-0 start-0 m-2">
                            ${priorityText}
                        </span>
                    </div>
                    <div class="card-body d-flex flex-column">
                        <h6 class="card-title">${item.productName}</h6>
                        <p class="card-text text-muted small">${item.productDescription || ''}</p>
                        <div class="mt-auto">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="h5 text-primary mb-0">$${item.productPrice}</span>
                                <span class="badge ${item.productStatus === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">
                                    ${item.productStatus}
                                </span>
                            </div>
                            ${item.notes ? `<small class="text-muted"><i class="fas fa-sticky-note"></i> ${item.notes}</small>` : ''}
                            <div class="mt-2">
                                <button class="btn btn-primary btn-sm me-2" onclick="window.location.href='product-detail.html?id=${item.productId}'">
                                    View Details
                                </button>
                                <button class="btn btn-outline-danger btn-sm wishlist-remove" 
                                        data-product-id="${item.productId}">
                                    Remove
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    // Get priority class for styling
    getPriorityClass(priority) {
        switch (priority) {
            case 3: return 'bg-danger';
            case 2: return 'bg-warning';
            case 1: return 'bg-info';
            default: return 'bg-secondary';
        }
    }

    // Get priority text
    getPriorityText(priority) {
        switch (priority) {
            case 3: return 'High';
            case 2: return 'Medium';
            case 1: return 'Low';
            default: return 'Normal';
        }
    }

    // Update pagination
    updatePagination(result) {
        const $pagination = $('#wishlist-pagination');
        $pagination.empty();

        if (result.totalPages > 1) {
            let paginationHTML = '<nav><ul class="pagination justify-content-center">';
            
            // Previous button
            if (result.number > 0) {
                paginationHTML += `<li class="page-item">
                    <a class="page-link" href="#" data-page="${result.number - 1}">Previous</a>
                </li>`;
            }

            // Page numbers
            for (let i = 0; i < result.totalPages; i++) {
                const activeClass = i === result.number ? 'active' : '';
                paginationHTML += `<li class="page-item ${activeClass}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>`;
            }

            // Next button
            if (result.number < result.totalPages - 1) {
                paginationHTML += `<li class="page-item">
                    <a class="page-link" href="#" data-page="${result.number + 1}">Next</a>
                </li>`;
            }

            paginationHTML += '</ul></nav>';
            $pagination.html(paginationHTML);
        }
    }

    // Search wishlist
    async searchWishlist(keyword, page = 0, size = 10) {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.showMessage('Please login to search wishlist', 'error');
                return;
            }

            const response = await fetch(
                `${this.apiBaseUrl}/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`,
                {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            const result = await response.json();

            if (response.ok) {
                this.displayWishlist(result.content || []);
                this.updatePagination(result);
            } else {
                this.showMessage(result.error || 'Search failed', 'error');
            }
        } catch (error) {
            console.error('Search wishlist error:', error);
            this.showMessage('Network error. Please try again.', 'error');
        }
    }

    // Navigate to wishlist page
    navigateToWishlist() {
        window.location.href = 'wishlist.html';
    }

    // Redirect to login
    redirectToLogin() {
        window.location.href = 'page-login.html';
    }

    // Show message
    showMessage(message, type) {
        const alertClass = type === 'error' ? 'alert-danger' : 'alert-success';
        const $message = $(`
            <div class="alert ${alertClass} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3" 
                 role="alert" style="z-index: 9999; min-width: 300px;">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `);
        
        // Append to body if messages-container doesn't exist
        if ($('.messages-container').length) {
            $('.messages-container').html($message);
        } else {
            $('body').append($message);
        }
        
        // Auto-hide after 3 seconds
        setTimeout(() => {
            $message.alert('close');
        }, 3000);
    }
}

// Initialize wishlist manager
let wishlistManager;

$(document).ready(function() {
    wishlistManager = new WishlistManager();
    
    // Handle wishlist page events
    if (window.location.pathname.includes('wishlist.html')) {
        wishlistManager.loadWishlist();
        
        // Search functionality
        $('#wishlist-search').on('input', function() {
            const keyword = $(this).val();
            if (keyword.length > 2) {
                wishlistManager.searchWishlist(keyword);
            } else if (keyword.length === 0) {
                wishlistManager.loadWishlist();
            }
        });
        
        // Pagination
        $(document).on('click', '.pagination a', function(e) {
            e.preventDefault();
            const page = $(this).data('page');
            wishlistManager.loadWishlist(page);
        });
        
        // Remove from wishlist
        $(document).on('click', '.wishlist-remove', async function() {
            const productId = $(this).data('product-id');
            const success = await wishlistManager.removeFromWishlist(productId);
            if (success) {
                // Reload wishlist after successful removal
                wishlistManager.loadWishlist();
            }
        });
    }
});

// Global functions for easy access
function toggleWishlist(productId) {
    if (wishlistManager) {
        wishlistManager.toggleWishlistItem(productId);
    }
}

function addToWishlist(productId, options = {}) {
    if (wishlistManager) {
        wishlistManager.addToWishlist(productId, options);
    }
}

function removeFromWishlist(productId) {
    if (wishlistManager) {
        wishlistManager.removeFromWishlist(productId);
    }
}
