// Homepage Product Loading
const HomepageProducts = {
    apiBaseUrl: 'http://localhost:8080/api',
    
    // Load products for homepage
    async loadProducts(category = 'all', limit = 8) {
        try {
            let url = `${this.apiBaseUrl}/products?page=0&size=${limit}`;
            
            // Add category filter if needed
            if (category !== 'all') {
                url += `&keyword=${encodeURIComponent(category)}`;
            }
            
            const response = await fetch(url);
            if (!response.ok) {
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
        
        return `
            <div class="col">
                <div class="product-card">
                    <button class="fav-btn wishlist-toggle" data-product-id="${product.id}" title="Add to Wishlist">
                        <i class="far fa-heart"></i>
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
        }
    },
    
    // Initialize homepage products
    async init() {
        // Load Men's products
        await this.renderProductsInTab('men-products', 'men', 4);
        
        // Load Women's products
        await this.renderProductsInTab('women-products', 'women', 4);
        
        // Load Kids' products
        await this.renderProductsInTab('kids-products', 'kids', 4);
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Initialize wishlist manager first
    if (typeof WishlistManager !== 'undefined') {
        wishlistManager = new WishlistManager();
    }
    
    // Load homepage products
    HomepageProducts.init();
});

// Make it globally available
window.HomepageProducts = HomepageProducts;

