// Admin Orders Management JavaScript

// Order Status Mapping
const ORDER_STATUS = {
    PENDING: 'PENDING',
    PROCESSING: 'PROCESSING',
    SHIPPED: 'SHIPPED',
    DELIVERED: 'DELIVERED',
    CANCELLED: 'CANCELLED'
};

// Status badge mapping
const STATUS_BADGE_MAP = {
    'PENDING': {
        class: 'badge-orange',
        icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" color="currentColor" fill="none">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"></circle>
                <path d="M12 8V12L14 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
              </svg>`,
        label: 'Chờ xử lý'
    },
    'PROCESSING': {
        class: 'badge-blue',
        icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" color="currentColor" fill="none">
                <path d="M12 2v4m0 12v4M4.93 4.93l2.83 2.83m8.48 8.48l2.83 2.83M2 12h4m12 0h4M4.93 19.07l2.83-2.83m8.48-8.48l2.83-2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>`,
        label: 'Đang xử lý'
    },
    'SHIPPED': {
        class: 'badge-primary',
        icon: `<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2"></path>
                <path d="M15 18H9"></path>
                <path d="M19 18h2a1 1 0 0 0 1-1v-3.65a1 1 0 0 0-.22-.624l-3.48-4.35A1 1 0 0 0 17.52 8H14"></path>
                <circle cx="17" cy="18" r="2"></circle>
                <circle cx="7" cy="18" r="2"></circle>
              </svg>`,
        label: 'Đang giao'
    },
    'DELIVERED': {
        class: 'badge-green',
        icon: `<svg xmlns="http://www.w3.org/2000/svg" height="20" width="20" viewBox="0 -960 960 960" fill="currentColor">
                <path d="M382-240 154-468l57-57 171 171 367-367 57 57-424 424Z"></path>
              </svg>`,
        label: 'Đã giao'
    },
    'CANCELLED': {
        class: 'badge-red',
        icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" color="currentColor" fill="none">
                <path d="M19.0005 4.99988L5.00049 18.9999M5.00049 4.99988L19.0005 18.9999" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
              </svg>`,
        label: 'Đã hủy'
    }
};

// Global variables
let allOrders = [];
let filteredOrders = [];
let currentFilter = 'all';
let searchQuery = '';

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    checkAuthentication();
    loadAllOrders();
    setupEventListeners();
});

// Check if user is authenticated as admin
function checkAuthentication() {
    const token = localStorage.getItem('token');
    const userRole = localStorage.getItem('role'); // Changed from 'userRole' to 'role'
    
    console.log('Token:', token ? 'exists' : 'missing');
    console.log('User Role:', userRole);
    
    if (!token) {
        alert('Bạn cần đăng nhập để truy cập trang này!');
        window.location.href = 'page-login.html';
        return;
    }
    
    // Check for both 'admin' and 'ADMIN' to be safe
    if (userRole !== 'admin' && userRole !== 'ADMIN') {
        alert('Bạn không có quyền truy cập trang này! Chỉ Admin mới có quyền.');
        window.location.href = 'page-login.html';
        return;
    }
}

// Setup event listeners
function setupEventListeners() {
    // Search functionality
    const searchInput = document.querySelector('input[type="search"]');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            searchQuery = e.target.value.toLowerCase();
            filterOrders();
        });
    }

    // Status filter
    const statusSelect = document.querySelector('select[name="statusFilter"]');
    if (statusSelect) {
        statusSelect.addEventListener('change', (e) => {
            currentFilter = e.target.value;
            filterOrders();
        });
    }
}

// Load all orders from API
async function loadAllOrders() {
    try {
        showLoading();
        const token = localStorage.getItem('token');
        
        const response = await fetch(window.AppConfig.getApiUrl('/orders'), {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load orders');
        }

        allOrders = await response.json();
        filteredOrders = [...allOrders];
        renderOrders();
        
    } catch (error) {
        console.error('Error loading orders:', error);
        showError('Không thể tải danh sách đơn hàng. Vui lòng thử lại sau.');
    } finally {
        hideLoading();
    }
}

// Filter orders based on search and status
function filterOrders() {
    filteredOrders = allOrders.filter(order => {
        // Filter by search query
        const matchesSearch = !searchQuery || 
            order.orderId.toString().includes(searchQuery) ||
            order.userName?.toLowerCase().includes(searchQuery) ||
            order.userEmail?.toLowerCase().includes(searchQuery);

        // Filter by status
        const matchesStatus = currentFilter === 'all' || 
            currentFilter === '' || 
            order.status === currentFilter;

        return matchesSearch && matchesStatus;
    });

    renderOrders();
}

// Render orders table
function renderOrders() {
    const tbody = document.querySelector('table tbody');
    
    if (!tbody) {
        console.error('Table tbody not found');
        return;
    }

    if (filteredOrders.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6">
                    <div class="empty-state">
                        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <circle cx="12" cy="12" r="10"></circle>
                            <line x1="12" y1="8" x2="12" y2="12"></line>
                            <line x1="12" y1="16" x2="12.01" y2="16"></line>
                        </svg>
                        <p class="mb-0">Không tìm thấy đơn hàng nào</p>
                        <small class="text-muted">Thử tìm kiếm với từ khóa khác</small>
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = filteredOrders.map(order => {
        const statusInfo = STATUS_BADGE_MAP[order.status] || STATUS_BADGE_MAP['PENDING'];
        const orderDate = new Date(order.orderDate).toLocaleDateString('vi-VN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        return `
            <tr data-order-id="${order.orderId}">
                <td data-label="Mã đơn hàng">
                    <div class="d-flex align-items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="me-2 text-primary">
                            <path d="M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2"></path>
                            <path d="M15 18H9"></path>
                            <path d="M19 18h2a1 1 0 0 0 1-1v-3.65a1 1 0 0 0-.22-.624l-3.48-4.35A1 1 0 0 0 17.52 8H14"></path>
                            <circle cx="17" cy="18" r="2"></circle>
                            <circle cx="7" cy="18" r="2"></circle>
                        </svg>
                        <a href="#" class="order-id-link" onclick="viewOrderDetail(${order.orderId}); return false;">#${order.orderId}</a>
                    </div>
                </td>
                <td data-label="Ngày đặt">
                    <span>${orderDate}</span>
                </td>
                <td data-label="Khách hàng">
                    <div class="customer-info">
                        <div class="customer-name">${order.userName || 'N/A'}</div>
                        <small class="customer-email">${order.userEmail || ''}</small>
                    </div>
                </td>
                <td data-label="Trạng thái">
                    <span class="badge ${statusInfo.class}">
                        ${statusInfo.icon}
                        ${statusInfo.label}
                    </span>
                </td>
                <td class="text-end" data-label="Tổng tiền">
                    <span class="price-display">
                        <span class="currency-label">VNĐ</span>
                        ${formatCurrency(order.totalPrice)}
                    </span>
                </td>
                <td class="text-end" data-label="Thao tác">
                    <div class="btn-action-group">
                        <button class="btn btn-sm btn-default" onclick="viewOrderDetail(${order.orderId})">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="me-1">
                                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                            Chi tiết
                        </button>
                        <div class="dropdown">
                            <button class="btn btn-sm btn-neutral dropdown-toggle" data-bs-toggle="dropdown">
                                Hành động
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                ${order.status !== 'DELIVERED' && order.status !== 'CANCELLED' ? `
                                    <li><a class="dropdown-item" href="#" onclick="updateOrderStatus(${order.orderId}, 'PROCESSING'); return false;">🔄 Đang xử lý</a></li>
                                    <li><a class="dropdown-item" href="#" onclick="updateOrderStatus(${order.orderId}, 'SHIPPED'); return false;">🚚 Đang giao</a></li>
                                    <li><a class="dropdown-item" href="#" onclick="updateOrderStatus(${order.orderId}, 'DELIVERED'); return false;">✅ Đã giao</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                ` : ''}
                                ${order.status !== 'CANCELLED' && order.status !== 'DELIVERED' ? `
                                    <li><a class="dropdown-item text-danger" href="#" onclick="cancelOrder(${order.orderId}); return false;">❌ Hủy đơn</a></li>
                                ` : ''}
                            </ul>
                        </div>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// View order detail
async function viewOrderDetail(orderId) {
    try {
        showLoading();
        const token = localStorage.getItem('token');
        
        const response = await fetch(window.AppConfig.getApiUrl(`/orders/${orderId}`), {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to load order detail');
        }

        const order = await response.json();
        showOrderDetailModal(order);
        
    } catch (error) {
        console.error('Error loading order detail:', error);
        showError('Không thể tải chi tiết đơn hàng.');
    } finally {
        hideLoading();
    }
}

// Show order detail modal
function showOrderDetailModal(order) {
    const statusInfo = STATUS_BADGE_MAP[order.status] || STATUS_BADGE_MAP['PENDING'];
    const orderDate = new Date(order.orderDate).toLocaleDateString('vi-VN');
    
    const modalHTML = `
        <div class="modal fade" id="orderDetailModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Chi tiết đơn hàng #${order.orderId}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row mb-4">
                            <div class="col-md-6">
                                <h6>Thông tin khách hàng</h6>
                                <p class="mb-1"><strong>Tên:</strong> ${order.userName || 'N/A'}</p>
                                <p class="mb-1"><strong>Email:</strong> ${order.userEmail || 'N/A'}</p>
                                <p class="mb-1"><strong>Số điện thoại:</strong> ${order.phoneNumber || 'N/A'}</p>
                            </div>
                            <div class="col-md-6">
                                <h6>Thông tin đơn hàng</h6>
                                <p class="mb-1"><strong>Ngày đặt:</strong> ${orderDate}</p>
                                <p class="mb-1"><strong>Trạng thái:</strong> 
                                    <span class="badge ${statusInfo.class}">${statusInfo.label}</span>
                                </p>
                                <p class="mb-1"><strong>Địa chỉ giao hàng:</strong> ${order.shippingAddress || 'N/A'}</p>
                            </div>
                        </div>
                        
                        <h6>Sản phẩm</h6>
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Sản phẩm</th>
                                        <th class="text-center">Số lượng</th>
                                        <th class="text-right">Đơn giá</th>
                                        <th class="text-right">Tổng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${order.orderItems.map(item => `
                                        <tr>
                                            <td>${item.productName}</td>
                                            <td class="text-center">${item.quantity}</td>
                                            <td class="text-right">${formatCurrency(item.price)} VNĐ</td>
                                            <td class="text-right">${formatCurrency(item.price * item.quantity)} VNĐ</td>
                                        </tr>
                                    `).join('')}
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="3" class="text-right"><strong>Tổng cộng:</strong></td>
                                        <td class="text-right"><strong>${formatCurrency(order.totalPrice)} VNĐ</strong></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    const existingModal = document.getElementById('orderDetailModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
    modal.show();
}

// Update order status
async function updateOrderStatus(orderId, newStatus) {
    if (!confirm(`Bạn có chắc muốn cập nhật trạng thái đơn hàng #${orderId}?`)) {
        return;
    }

    try {
        showLoading();
        const token = localStorage.getItem('token');
        
        const response = await fetch(window.AppConfig.getApiUrl(`/orders/${orderId}/status`), {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: newStatus })
        });

        if (!response.ok) {
            throw new Error('Failed to update order status');
        }

        showSuccess('Cập nhật trạng thái đơn hàng thành công!');
        await loadAllOrders(); // Reload orders
        
    } catch (error) {
        console.error('Error updating order status:', error);
        showError('Không thể cập nhật trạng thái đơn hàng.');
    } finally {
        hideLoading();
    }
}

// Cancel order
async function cancelOrder(orderId) {
    if (!confirm(`Bạn có chắc muốn hủy đơn hàng #${orderId}?`)) {
        return;
    }

    try {
        showLoading();
        const token = localStorage.getItem('token');
        
        const response = await fetch(window.AppConfig.getApiUrl(`/orders/${orderId}/cancel`), {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed to cancel order');
        }

        showSuccess('Đơn hàng đã được hủy thành công!');
        await loadAllOrders(); // Reload orders
        
    } catch (error) {
        console.error('Error cancelling order:', error);
        showError('Không thể hủy đơn hàng.');
    } finally {
        hideLoading();
    }
}

// Utility functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount);
}

function showLoading() {
    const loadingHTML = `
        <div id="loadingOverlay" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 9999;">
            <div class="spinner-border text-light" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', loadingHTML);
}

function hideLoading() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.remove();
    }
}

function showSuccess(message) {
    alert(message); // You can replace this with a nicer toast notification
}

function showError(message) {
    alert(message); // You can replace this with a nicer toast notification
}
