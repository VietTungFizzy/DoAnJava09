/**
 * Payment Success Page JavaScript
 * Handles dynamic content loading and user interactions
 */
document.addEventListener('DOMContentLoaded', function() {
    // Initialize the page
    initializePaymentSuccessPage();
});

function initializePaymentSuccessPage() {
    // Extract URL parameters for order details
    const urlParams = new URLSearchParams(window.location.search);
    const sessionId = urlParams.get('session_id');
    const orderId = urlParams.get('order_id');
    
    // Load order details
    loadOrderDetails(sessionId, orderId);
    
    // Setup event listeners
    setupEventListeners();
    
    // Show success animation
    showSuccessAnimation();
}

function loadOrderDetails(sessionId, orderId) {
    // If we have real session/order data, fetch from backend
    if (sessionId || orderId) {
        fetchOrderDetailsFromBackend(sessionId, orderId);
    } else {
        // Load mock data for demonstration
        loadMockOrderData();
    }
}

async function fetchOrderDetailsFromBackend(sessionId, orderId) {
    try {
        // Show loading state
        showLoadingState();
        
        const response = await fetch(`/api/orders/${orderId || 'session/' + sessionId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to fetch order details');
        }
        
        const orderData = await response.json();
        displayOrderDetails(orderData);
        
    } catch (error) {
        console.error('Error fetching order details:', error);
        // Fallback to mock data if backend fails
        loadMockOrderData();
    } finally {
        hideLoadingState();
    }
}

function loadMockOrderData() {
    // Mock order data for demonstration
    const mockOrderData = {
        orderNumber: '#ORD-2025-001234',
        transactionId: 'TXN-2025-001234567',
        paymentMethod: 'Credit Card (**** 1234)',
        paymentDate: new Date().toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        }),
        items: [
            {
                id: 1,
                name: 'Great product name goes here',
                image: 'images/product-basic/1.png',
                price: 46.00,
                quantity: 1,
                attributes: 'Size: Medium, Color: Blue'
            },
            {
                id: 2,
                name: 'Cutting-edge device title goes here',
                image: 'images/product-basic/2.png',
                price: 46.00,
                quantity: 1,
                attributes: 'Size: Large, Material: Plastic'
            }
        ],
        subtotal: 92.00,
        shipping: 9.00,
        tax: 3.90,
        total: 104.90,
        shippingAddress: {
            name: 'John Doe',
            address: '123 Main Street',
            city: 'Springfield',
            zip: '12345',
            country: 'United States'
        },
        estimatedDelivery: 'January 16-18, 2025',
        shippingMethod: 'Standard Delivery (7-10 business days)'
    };
    
    displayOrderDetails(mockOrderData);
}

function displayOrderDetails(orderData) {
    // Update order number
    const orderNumberElement = document.getElementById('order-number');
    if (orderNumberElement) {
        orderNumberElement.textContent = orderData.orderNumber;
    }
    
    // Update payment information
    updatePaymentInfo(orderData);
    
    // Update order items
    updateOrderItems(orderData.items);
    
    // Update order totals
    updateOrderTotals(orderData);
    
    // Update shipping information
    updateShippingInfo(orderData);
}

function updatePaymentInfo(orderData) {
    const paymentMethodElement = document.getElementById('payment-method');
    const transactionIdElement = document.getElementById('transaction-id');
    const paymentDateElement = document.getElementById('payment-date');
    
    if (paymentMethodElement) {
        paymentMethodElement.textContent = orderData.paymentMethod;
    }
    
    if (transactionIdElement) {
        transactionIdElement.textContent = orderData.transactionId;
    }
    
    if (paymentDateElement) {
        paymentDateElement.textContent = orderData.paymentDate;
    }
}

function updateOrderItems(items) {
    const orderItemsContainer = document.getElementById('order-items');
    if (!orderItemsContainer || !items) return;
    
    let itemsHTML = '';
    
    items.forEach(item => {
        itemsHTML += `
            <div class="order-item d-flex align-items-center gap-3 py-3 border-bottom">
                <div class="item-image">
                    <img src="${item.image}" alt="${item.name}" class="w-20 h-20 rounded bg-neutral-200">
                </div>
                <div class="item-details flex-grow">
                    <h5 class="mb-1">${item.name}</h5>
                    <p class="text-secondary mb-0">${item.attributes}</p>
                    <p class="text-secondary mb-0">Quantity: ${item.quantity}</p>
                </div>
                <div class="item-price">
                    <span class="font-bold text-lg">$${item.price.toFixed(2)}</span>
                </div>
            </div>
        `;
    });
    
    orderItemsContainer.innerHTML = itemsHTML;
}

function updateOrderTotals(orderData) {
    const subtotalElement = document.getElementById('subtotal');
    const shippingElement = document.getElementById('shipping');
    const taxElement = document.getElementById('tax');
    const totalElement = document.getElementById('total');
    
    if (subtotalElement) {
        subtotalElement.textContent = `$${orderData.subtotal.toFixed(2)}`;
    }
    
    if (shippingElement) {
        shippingElement.textContent = `$${orderData.shipping.toFixed(2)}`;
    }
    
    if (taxElement) {
        taxElement.textContent = `$${orderData.tax.toFixed(2)}`;
    }
    
    if (totalElement) {
        totalElement.textContent = `$${orderData.total.toFixed(2)}`;
    }
}

function updateShippingInfo(orderData) {
    const shippingInfoContainer = document.getElementById('shipping-info');
    if (!shippingInfoContainer || !orderData.shippingAddress) return;
    
    const shippingHTML = `
        <div class="mb-3">
            <strong>Delivery Address:</strong>
            <p class="mb-1">${orderData.shippingAddress.name}</p>
            <p class="mb-1">${orderData.shippingAddress.address}</p>
            <p class="mb-1">${orderData.shippingAddress.city}, ${orderData.shippingAddress.zip}</p>
            <p class="mb-0">${orderData.shippingAddress.country}</p>
        </div>
        <div class="mb-3">
            <strong>Estimated Delivery:</strong>
            <p class="mb-0">${orderData.estimatedDelivery}</p>
        </div>
        <div>
            <strong>Shipping Method:</strong>
            <p class="mb-0">${orderData.shippingMethod}</p>
        </div>
    `;
    
    shippingInfoContainer.innerHTML = shippingHTML;
}

function setupEventListeners() {
    // Track Order Button
    const trackOrderBtn = document.getElementById('track-order-btn');
    if (trackOrderBtn) {
        trackOrderBtn.addEventListener('click', function(e) {
            e.preventDefault();
            handleTrackOrder();
        });
    }
    
    // Download Receipt Button
    const downloadReceiptBtn = document.getElementById('download-receipt-btn');
    if (downloadReceiptBtn) {
        downloadReceiptBtn.addEventListener('click', function(e) {
            e.preventDefault();
            handleDownloadReceipt();
        });
    }
    
    // Add click tracking for analytics
    trackButtonClicks();
}

function handleTrackOrder() {
    const orderNumber = document.getElementById('order-number')?.textContent;
    
    if (orderNumber) {
        // Add loading state
        const trackBtn = document.getElementById('track-order-btn');
        addLoadingState(trackBtn);
        
        // Simulate tracking page redirect
        setTimeout(() => {
            // In a real app, this would redirect to a tracking page
            // window.location.href = `/track-order?order=${orderNumber}`;
            
            // For demo, show an alert
            alert(`Tracking information for order ${orderNumber} would be displayed here.`);
            removeLoadingState(trackBtn);
        }, 1000);
    }
}

function handleDownloadReceipt() {
    const downloadBtn = document.getElementById('download-receipt-btn');
    addLoadingState(downloadBtn);
    
    // Simulate receipt generation and download
    setTimeout(() => {
        try {
            generateAndDownloadReceipt();
        } catch (error) {
            console.error('Error generating receipt:', error);
            alert('Sorry, there was an error generating your receipt. Please try again.');
        } finally {
            removeLoadingState(downloadBtn);
        }
    }, 1500);
}

function generateAndDownloadReceipt() {
    // Get order data for receipt
    const orderNumber = document.getElementById('order-number')?.textContent || '#ORD-UNKNOWN';
    const total = document.getElementById('total')?.textContent || '$0.00';
    const paymentDate = document.getElementById('payment-date')?.textContent || 'Unknown';
    
    // Create receipt content
    const receiptContent = `
PAYMENT RECEIPT
================

Order Number: ${orderNumber}
Date: ${paymentDate}
Total Amount: ${total}

Thank you for your purchase!

For customer support, please contact us at:
Email: support@brandname.com
Phone: 1-800-BRANDNAME

This is an automatically generated receipt.
    `.trim();
    
    // Create and download file
    const blob = new Blob([receiptContent], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `receipt-${orderNumber.replace('#', '')}.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

function showSuccessAnimation() {
    // Add entrance animation to success icon
    const successIcon = document.querySelector('.success-icon');
    if (successIcon) {
        successIcon.style.transform = 'scale(0)';
        successIcon.style.opacity = '0';
        
        setTimeout(() => {
            successIcon.style.transition = 'all 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55)';
            successIcon.style.transform = 'scale(1)';
            successIcon.style.opacity = '1';
        }, 200);
    }
    
    // Animate other elements
    animateElements();
}

function animateElements() {
    const elements = document.querySelectorAll('.card, .order-number');
    elements.forEach((element, index) => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            element.style.transition = 'all 0.6s ease';
            element.style.opacity = '1';
            element.style.transform = 'translateY(0)';
        }, 300 + (index * 100));
    });
}

function trackButtonClicks() {
    // Track button clicks for analytics
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function() {
            const buttonText = this.textContent.trim();
            console.log(`Button clicked: ${buttonText}`);
            
            // In a real app, you would send this to your analytics service
            // analytics.track('button_click', { button_name: buttonText });
        });
    });
}

function addLoadingState(button) {
    if (button) {
        button.classList.add('loading');
        button.disabled = true;
        
        // Store original text
        button.dataset.originalText = button.innerHTML;
        
        // Update button text
        const loadingText = button.textContent.includes('Track') ? 'Loading...' : 
                           button.textContent.includes('Download') ? 'Generating...' : 'Loading...';
        button.innerHTML = `
            <svg class="animate-spin mr-2" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 12a9 9 0 11-6.219-8.56"/>
            </svg>
            ${loadingText}
        `;
    }
}

function removeLoadingState(button) {
    if (button && button.dataset.originalText) {
        button.classList.remove('loading');
        button.disabled = false;
        button.innerHTML = button.dataset.originalText;
        delete button.dataset.originalText;
    }
}

function showLoadingState() {
    // Show loading skeleton or spinner for the entire page
    const loadingHTML = `
        <div class="loading-overlay" style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.9);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
        ">
            <div style="text-align: center;">
                <div style="
                    width: 40px;
                    height: 40px;
                    border: 4px solid #f3f4f6;
                    border-top: 4px solid #6163fe;
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 1rem;
                "></div>
                <p>Loading order details...</p>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', loadingHTML);
}

function hideLoadingState() {
    const loadingOverlay = document.querySelector('.loading-overlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}

// Utility function to format currency
function formatCurrency(amount, currency = 'USD') {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

// Utility function to format date
function formatDate(date) {
    return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Export functions for testing (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        loadOrderDetails,
        displayOrderDetails,
        formatCurrency,
        formatDate
    };
}