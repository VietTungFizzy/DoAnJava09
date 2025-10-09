// Profile page constants
const PROFILE_CONFIG = {
    MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
    ALLOWED_IMAGE_TYPES: ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
};

// Profile page functionality
$(function() {
    // Initialize profile page
    initializeProfile();
    
    // Load user profile data
    loadUserProfile();
    
    // Load addresses if available
    loadAddresses();
    
    // Load user achievements
    loadUserAchievements();
    
    // Load top products
    loadTopProducts();
});

function initializeProfile() {
    // Avatar preview logic with file size validation
    $("#avatarInput").on("change", function(e) {
        const file = this.files[0];
        if (file && file.type.startsWith('image/')) {
            // Validate file size using constant
            if (file.size > PROFILE_CONFIG.MAX_FILE_SIZE) {
                showNotification(`Kích thước file không được vượt quá ${PROFILE_CONFIG.MAX_FILE_SIZE / (1024 * 1024)}MB`, 'error');
                this.value = '';
                return;
            }
            
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#avatarImg").attr("src", e.target.result);
            }
            reader.readAsDataURL(file);
        }
    });
    
    // Radio buttons for gender - browser handles exclusivity automatically
    // No manual handler needed; browser handles exclusivity for radio buttons with the same name
    
    // Profile form submission
    $('#profileForm').on('submit', function(e) {
        e.preventDefault();
        updateProfile();
    });
    
    // Address form submission
    $('#addAddressForm').on('submit', function(e) {
        e.preventDefault();
        addAddress();
    });
    
    // Event delegation for dynamic elements instead of global functions
    $(document).on('click', '[data-action="edit-address"]', function() {
        const addressId = $(this).data('address-id');
        editAddress(addressId);
    });
    
    $(document).on('click', '[data-action="delete-address"]', function() {
        const addressId = $(this).data('address-id');
        showDeleteAddressModal(addressId);
    });
    
    // Confirm delete address modal handler
    $('#confirmDeleteAddressBtn').on('click', function() {
        const addressId = $('#confirmDeleteAddressModal').data('addressId');
        deleteAddress(addressId);
    });
}

function loadUserProfile() {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    
    showLoading(true);
    
    $.ajax({
        url: `${API_BASE_URL}/user/profile`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        success: function(response) {
            populateProfileForm(response);
            showLoading(false);
        },
        error: function(xhr) {
            console.log('API not available, using mock data:', xhr);
            // Fallback to mock data
            loadMockProfileData();
            showLoading(false);
        }
    }).catch(function(error) {
        console.log('Error in loadUserProfile:', error);
        loadMockProfileData();
        showLoading(false);
    });
}

function loadMockProfileData() {
    console.log('Loading mock profile data...');
    const mockData = {
        id: 1,
        name: "Nguyễn Văn A",
        email: "user@example.com",
        phone: "0123456789",
        role: "buyer",
        status: "active",
        createdAt: "2023-01-01T00:00:00",
        summary: {
            totalOrders: 15,
            totalSpent: 2500000,
            favoriteProducts: 8,
            loyaltyPoints: 1250
        },
        addresses: [
            {
                id: 1,
                type: "shipping",
                label: "Nhà riêng",
                fullName: "Nguyễn Văn A",
                phone: "0123456789",
                addressLine1: "123 Đường ABC",
                ward: "Phường 1",
                district: "Quận 1",
                city: "TP.HCM",
                isDefault: true
            }
        ]
    };
    
    populateProfileForm(mockData);
    populateAddresses(mockData.addresses);
    populateUserSummary(mockData.summary);
}

function populateProfileForm(data) {
    $('#name').val(data.name || '');
    $('#email').val(data.email || '');
    $('#phone').val(data.phone || '');
    
    // Update profile summary if available
    if (data.summary) {
        populateUserSummary(data.summary);
    }
}

function populateUserSummary(summary) {
    $('#totalOrders').text(summary.totalOrders || 0);
    $('#totalSpent').text(formatCurrency(summary.totalSpent || 0));
    $('#favoriteProducts').text(summary.favoriteProducts || 0);
    $('#loyaltyPoints').text(summary.loyaltyPoints || 0);
}

function loadAddresses() {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    
    $.ajax({
        url: `${API_BASE_URL}/user/addresses`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        success: function(response) {
            populateAddresses(response);
        },
        error: function(xhr) {
            console.error('Error loading addresses:', xhr);
            // Load mock addresses
            populateAddresses([]);
        }
    });
}

function populateAddresses(addresses) {
    const container = $('#addressesList');
    container.empty();
    
    if (addresses.length === 0) {
        container.append('<p class="text-muted">Chưa có địa chỉ nào được lưu.</p>');
        return;
    }
    
    addresses.forEach(address => {
        const addressHtml = `
            <div class="address-item border p-3 mb-2 rounded">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h6 class="mb-1">${address.label || 'Địa chỉ'} ${address.isDefault ? '<span class="badge bg-primary">Mặc định</span>' : ''}</h6>
                        <p class="mb-1"><strong>${address.fullName}</strong></p>
                        <p class="mb-1">${address.phone}</p>
                        <p class="mb-0 text-muted">${address.addressLine1}, ${address.ward}, ${address.district}, ${address.city}</p>
                    </div>
                    <div class="btn-group">
                        <button class="btn btn-sm btn-outline-primary" data-action="edit-address" data-address-id="${address.id}">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" data-action="delete-address" data-address-id="${address.id}">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
        container.append(addressHtml);
    });
}

function addAddress() {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    const formData = new FormData(document.getElementById('addAddressForm'));
    
    showLoading(true);
    
    $.ajax({
        url: `${API_BASE_URL}/user/addresses`,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            showNotification('Địa chỉ đã được thêm thành công!', 'success');
            $('#addAddressModal').modal('hide');
            $('#addAddressForm')[0].reset();
            loadAddresses();
        },
        error: function(xhr) {
            showNotification('Có lỗi xảy ra khi thêm địa chỉ', 'error');
        },
        complete: function() {
            showLoading(false);
        }
    });
}

function editAddress(addressId) {
    // Implementation for editing address
    console.log('Edit address:', addressId);
    // This would populate the edit modal with address data
}

// Custom modal for delete confirmation instead of native confirm()
function showDeleteAddressModal(addressId) {
    // Store addressId in modal data
    $('#confirmDeleteAddressModal').data('addressId', addressId);
    $('#confirmDeleteAddressModal').modal('show');
}

function deleteAddress(addressId) {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    
    $('#confirmDeleteAddressModal').modal('hide');
    showLoading(true);
    
    $.ajax({
        url: `${API_BASE_URL}/user/addresses/${addressId}`,
        type: 'DELETE',
        success: function(response) {
            showNotification('Địa chỉ đã được xóa thành công!', 'success');
            loadAddresses();
        },
        error: function(xhr) {
            showNotification('Có lỗi xảy ra khi xóa địa chỉ', 'error');
        },
        complete: function() {
            showLoading(false);
        }
    });
}

function updateProfile() {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    const formData = new FormData(document.getElementById('profileForm'));
    
    showLoading(true);
    
    $.ajax({
        url: `${API_BASE_URL}/user/profile`,
        type: 'PUT',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            showNotification('Thông tin đã được cập nhật thành công!', 'success');
        },
        error: function(xhr) {
            showNotification('Có lỗi xảy ra khi cập nhật thông tin', 'error');
        },
        complete: function() {
            showLoading(false);
        }
    });
}

function loadUserAchievements() {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    
    $.ajax({
        url: `${API_BASE_URL}/user/achievements`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        success: function(response) {
            populateAchievements(response);
        },
        error: function(xhr) {
            console.error('Error loading achievements:', xhr);
            // Load mock achievements
            populateAchievements([]);
        }
    });
}

function populateAchievements(achievements) {
    const container = $('#achievementsList');
    container.empty();
    
    if (achievements.length === 0) {
        container.append('<p class="text-muted">Chưa có thành tựu nào.</p>');
        return;
    }
    
    achievements.forEach(achievement => {
        const achievementHtml = `
            <div class="achievement-item p-2 mb-2 border rounded">
                <i class="${achievement.icon} text-warning"></i>
                <span class="ms-2">${achievement.title}</span>
            </div>
        `;
        container.append(achievementHtml);
    });
}

function loadTopProducts() {
    const API_BASE_URL = (typeof CONFIG !== 'undefined') ? CONFIG.API_BASE_URL : 'http://localhost:8081/api';
    
    $.ajax({
        url: `${API_BASE_URL}/user/top-products`,
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        success: function(response) {
            populateTopProducts(response);
        },
        error: function(xhr) {
            console.error('Error loading top products:', xhr);
            // Load mock products
            populateTopProducts([]);
        }
    });
}

function populateTopProducts(products) {
    const container = $('#topProductsList');
    container.empty();
    
    if (products.length === 0) {
        container.append('<p class="text-muted">Chưa có sản phẩm yêu thích.</p>');
        return;
    }
    
    products.forEach(product => {
        const productHtml = `
            <div class="product-item d-flex p-2 mb-2 border rounded">
                <img src="${product.imageUrl || '/images/placeholder.jpg'}" alt="${product.name}" class="product-thumb me-3" style="width: 50px; height: 50px; object-fit: cover;">
                <div>
                    <h6 class="mb-1">${product.name}</h6>
                    <p class="mb-0 text-muted">${formatCurrency(product.price)}</p>
                </div>
            </div>
        `;
        container.append(productHtml);
    });
}

// Utility functions
function showLoading(show) {
    if (show) {
        $('.loading-spinner').removeClass('d-none');
    } else {
        $('.loading-spinner').addClass('d-none');
    }
}

function showNotification(message, type = 'info') {
    // Create notification element
    const notification = $(`
        <div class="alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `);
    
    $('body').append(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        notification.alert('close');
    }, 5000);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}