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
                await this.renderProductsInTab(`${tabId}-products`, categoryName, 8);
            }
        });
    },
    
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
            await this.renderProductsInTab(`${tabId}-products`, firstCategory.name, 8);
        }
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

