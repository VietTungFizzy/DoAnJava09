// Application Configuration
const AppConfig = {
    // API Configuration
    api: {
        baseUrl: window.location.origin.includes('localhost') 
            ? 'http://localhost:8080/api' 
            : '/api',
        endpoints: {
            categories: '/categories',
            products: '/products',
            users: '/users'
        }
    },
    
    // Environment detection
    isDevelopment: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1',
    isProduction: !this.isDevelopment,
    
    // Get full API URL for a specific endpoint
    getApiUrl: function(endpoint) {
        return this.api.baseUrl + endpoint;
    },
    
    // Get categories API URL
    getCategoriesUrl: function() {
        return this.getApiUrl(this.api.endpoints.categories);
    },

    // Separate backend (port 8087) for product listing used on category page
    getAllProductsUrl: function() {
        return 'http://localhost:8087/api/products/all';
    }
};

// Make it globally available
window.AppConfig = AppConfig;
