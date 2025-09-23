// Category Page JavaScript
class CategoryManager {
    constructor() {
        this.currentCategory = null;
        this.products = [];
        this.filteredProducts = [];
        this.currentPage = 1;
        this.itemsPerPage = 12;
        this.currentView = 'grid';
        this.filters = {
            priceRange: [0, 10000000],
            brands: [],
            sizes: [],
            colors: [],
            materials: [],
            genders: [],
            seasons: [],
            styles: [],
            fits: []
        };
        this.sortBy = 'newest';
        
        this.init();
    }

    init() {
        this.loadCategoryFromURL();
        this.setupEventListeners();
        this.loadProducts();
        this.loadFeaturedProducts();
        this.setupFilters();
    }

    loadCategoryFromURL() {
        const urlParams = new URLSearchParams(window.location.search);
        const categoryId = urlParams.get('category');
        const categoryName = urlParams.get('name');
        
        if (categoryId) {
            this.currentCategory = {
                id: categoryId,
                name: categoryName || 'Danh mục sản phẩm'
            };
            this.updateCategoryHeader();
        }
    }

    updateCategoryHeader() {
        if (this.currentCategory) {
            document.getElementById('categoryTitle').textContent = this.currentCategory.name;
            document.getElementById('breadcrumbCategory').textContent = this.currentCategory.name;
        }
    }

    setupEventListeners() {
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', (e) => {
            this.handleSearch(e.target.value);
        });

        // Sorting
        document.getElementById('sortSelect').addEventListener('change', (e) => {
            this.sortBy = e.target.value;
            this.applyFiltersAndSort();
        });

        // Price range
        document.getElementById('priceRange').addEventListener('input', (e) => {
            this.updatePriceRange(e.target.value);
        });
    }

    async loadProducts() {
        try {
            this.showLoading();
            
            // Simulate API call - replace with actual API endpoint
            const response = await this.fetchProducts();
            this.products = response.products || [];
            this.filteredProducts = [...this.products];
            
            this.updateProductCount();
            this.renderProducts();
            this.setupFilters();
            this.renderPagination();
            
        } catch (error) {
            console.error('Error loading products:', error);
            this.showError('Không thể tải sản phẩm. Vui lòng thử lại.');
        }
    }

    async fetchProducts() {
        // Mock data - replace with actual API call
        return {
            products: [
                {
                    id: 1,
                    name: 'iPhone 15 Pro Max',
                    price: 29990000,
                    originalPrice: 32990000,
                    discount: 9,
                    rating: 4.8,
                    reviewCount: 1250,
                    images: [
                        'images/product-tech/iphone-15-1.jpg',
                        'images/product-tech/iphone-15-2.jpg',
                        'images/product-tech/iphone-15-3.jpg'
                    ],
                    brand: 'Apple',
                    color: 'Titanium',
                    category: 'smartphone',
                    subcategory: 'iphone',
                    featured: true,
                    inStock: true,
                    description: 'iPhone 15 Pro Max với chip A17 Pro mạnh mẽ'
                },
                {
                    id: 2,
                    name: 'Samsung Galaxy S24 Ultra',
                    price: 24990000,
                    originalPrice: 27990000,
                    discount: 11,
                    rating: 4.7,
                    reviewCount: 980,
                    images: [
                        'images/product-tech/galaxy-s24-1.jpg',
                        'images/product-tech/galaxy-s24-2.jpg'
                    ],
                    brand: 'Samsung',
                    color: 'Black',
                    category: 'smartphone',
                    subcategory: 'android',
                    featured: true,
                    inStock: true,
                    description: 'Galaxy S24 Ultra với camera 200MP'
                },
                {
                    id: 3,
                    name: 'MacBook Pro M3',
                    price: 45990000,
                    originalPrice: 49990000,
                    discount: 8,
                    rating: 4.9,
                    reviewCount: 650,
                    images: [
                        'images/product-tech/macbook-pro-1.jpg',
                        'images/product-tech/macbook-pro-2.jpg',
                        'images/product-tech/macbook-pro-3.jpg'
                    ],
                    brand: 'Apple',
                    color: 'Space Gray',
                    category: 'laptop',
                    subcategory: 'macbook',
                    featured: false,
                    inStock: true,
                    description: 'MacBook Pro với chip M3 Pro hiệu năng cao',
                    ram: '16GB',
                    cpu: 'M3 Pro'
                },
                {
                    id: 4,
                    name: 'Áo thun nam Nike Dri-FIT',
                    price: 450000,
                    originalPrice: 600000,
                    discount: 25,
                    rating: 4.6,
                    reviewCount: 850,
                    images: [
                        'images/product-cloth/nike-tshirt-1.jpg',
                        'images/product-cloth/nike-tshirt-2.jpg',
                        'images/product-cloth/nike-tshirt-3.jpg'
                    ],
                    brand: 'Nike',
                    color: 'Black',
                    category: 'clothing',
                    subcategory: 'tshirt',
                    featured: true,
                    inStock: true,
                    description: 'Áo thun thể thao Nike Dri-FIT thoáng khí',
                    size: ['S', 'M', 'L', 'XL', 'XXL'],
                    material: 'Polyester',
                    gender: 'Men',
                    season: 'All-season',
                    style: 'Sport',
                    fit: 'Regular'
                },
                {
                    id: 5,
                    name: 'Quần jeans nữ Levi\'s 501',
                    price: 1200000,
                    originalPrice: 1500000,
                    discount: 20,
                    rating: 4.7,
                    reviewCount: 1200,
                    images: [
                        'images/product-cloth/levis-jeans-1.jpg',
                        'images/product-cloth/levis-jeans-2.jpg'
                    ],
                    brand: 'Levi\'s',
                    color: 'Blue',
                    category: 'clothing',
                    subcategory: 'jeans',
                    featured: false,
                    inStock: true,
                    description: 'Quần jeans classic Levi\'s 501',
                    size: ['24', '25', '26', '27', '28', '29', '30'],
                    material: 'Denim',
                    gender: 'Women',
                    season: 'All-season',
                    style: 'Casual',
                    fit: 'Regular'
                },
                {
                    id: 6,
                    name: 'Áo khoác nữ Zara',
                    price: 1800000,
                    originalPrice: 2200000,
                    discount: 18,
                    rating: 4.4,
                    reviewCount: 650,
                    images: [
                        'images/product-cloth/zara-jacket-1.jpg',
                        'images/product-cloth/zara-jacket-2.jpg',
                        'images/product-cloth/zara-jacket-3.jpg'
                    ],
                    brand: 'Zara',
                    color: 'Beige',
                    category: 'clothing',
                    subcategory: 'jacket',
                    featured: false,
                    inStock: true,
                    description: 'Áo khoác nữ Zara phong cách',
                    size: ['XS', 'S', 'M', 'L'],
                    material: 'Polyester',
                    gender: 'Women',
                    season: 'Fall',
                    style: 'Casual',
                    fit: 'Slim'
                }
            ]
        };
    }

    async loadFeaturedProducts() {
        const featuredProducts = this.products.filter(product => product.featured);
        if (featuredProducts.length > 0) {
            document.getElementById('featuredSection').style.display = 'block';
            this.renderFeaturedProducts(featuredProducts);
        }
    }

    renderFeaturedProducts(products) {
        const container = document.getElementById('featuredProducts');
        container.innerHTML = products.slice(0, 4).map(product => `
            <div class="col-md-3 col-sm-6 mb-4">
                <div class="product-card featured-card">
                    <div class="product-image-container">
                        <img src="${product.images[0]}" alt="${product.name}" class="product-image">
                        <div class="product-badges">
                            <span class="badge bg-warning">Nổi bật</span>
                            ${product.discount > 0 ? `<span class="badge bg-danger">-${product.discount}%</span>` : ''}
                        </div>
                        <button class="fav-btn" onclick="toggleFavorite(${product.id})">
                            <i class="far fa-heart"></i>
                        </button>
                    </div>
                    <div class="product-info">
                        <h6 class="product-name">${product.name}</h6>
                        <div class="product-price">
                            <span class="current-price">${this.formatPrice(product.price)}</span>
                            ${product.originalPrice > product.price ? `<span class="original-price">${this.formatPrice(product.originalPrice)}</span>` : ''}
                        </div>
                        <div class="product-rating">
                            <div class="stars">
                                ${this.renderStars(product.rating)}
                            </div>
                            <span class="rating-text">(${product.reviewCount})</span>
                        </div>
                        <button class="btn btn-primary w-100 mt-2" onclick="viewProduct(${product.id})">
                            Xem chi tiết
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    setupFilters() {
        this.setupBrandFilter();
        this.setupColorFilter();
        this.setupSizeFilter();
        this.setupSubcategoriesFilter();
        this.setupCategorySpecificFilters();
    }

    setupBrandFilter() {
        const brands = [...new Set(this.products.map(p => p.brand))];
        const container = document.querySelector('#brandFilter .filter-options');
        container.innerHTML = brands.map(brand => `
            <div class="form-check">
                <input class="form-check-input" type="checkbox" value="${brand}" id="brand-${brand}">
                <label class="form-check-label" for="brand-${brand}">
                    ${brand}
                </label>
            </div>
        `).join('');
    }

    setupColorFilter() {
        const colors = [...new Set(this.products.map(p => p.color))];
        const container = document.querySelector('#colorFilter .color-options');
        container.innerHTML = colors.map(color => `
            <div class="color-option" data-color="${color}" onclick="toggleColorFilter('${color}')">
                <div class="color-circle" style="background-color: ${this.getColorValue(color)}"></div>
                <span>${color}</span>
            </div>
        `).join('');
    }

    setupSizeFilter() {
        const sizes = [...new Set(this.products.flatMap(p => p.size || []))];
        if (sizes.length > 0) {
            document.getElementById('sizeFilter').style.display = 'block';
            const container = document.querySelector('#sizeFilter .filter-options');
            container.innerHTML = sizes.map(size => `
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="${size}" id="size-${size}">
                    <label class="form-check-label" for="size-${size}">
                        ${size}
                    </label>
                </div>
            `).join('');
        }
    }

    setupSubcategoriesFilter() {
        const subcategories = [...new Set(this.products.map(p => p.subcategory))];
        const container = document.querySelector('#subcategoriesFilter .subcategories-list');
        container.innerHTML = subcategories.map(sub => `
            <div class="subcategory-item" onclick="filterBySubcategory('${sub}')">
                <i class="fas fa-chevron-right"></i>
                ${this.formatSubcategoryName(sub)}
            </div>
        `).join('');
    }

    setupCategorySpecificFilters() {
        const category = this.currentCategory?.id || 'general';
        
        // Hide all category-specific filters first
        document.getElementById('sizeFilter').style.display = 'none';
        document.getElementById('materialFilter').style.display = 'none';
        document.getElementById('genderFilter').style.display = 'none';
        document.getElementById('seasonFilter').style.display = 'none';
        document.getElementById('styleFilter').style.display = 'none';
        document.getElementById('fitFilter').style.display = 'none';

        // Show relevant filters for clothing categories
        if (['clothing', 'shoes', 'accessories'].includes(category)) {
            document.getElementById('sizeFilter').style.display = 'block';
            document.getElementById('materialFilter').style.display = 'block';
            document.getElementById('genderFilter').style.display = 'block';
            document.getElementById('seasonFilter').style.display = 'block';
            document.getElementById('styleFilter').style.display = 'block';
            document.getElementById('fitFilter').style.display = 'block';
        }
    }

    renderProducts() {
        const container = document.getElementById('productsContainer');
        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        const productsToShow = this.filteredProducts.slice(startIndex, endIndex);

        if (productsToShow.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">🔍</div>
                    <h3 class="empty-state-title">Không tìm thấy sản phẩm</h3>
                    <p class="empty-state-message">Hãy thử điều chỉnh bộ lọc hoặc từ khóa tìm kiếm</p>
                </div>
            `;
            return;
        }

        container.innerHTML = productsToShow.map(product => this.renderProductCard(product)).join('');
    }

    renderProductCard(product) {
        const viewClass = this.currentView === 'list' ? 'list-view' : 'grid-view';
        return `
            <div class="product-card ${viewClass}" onclick="viewProduct(${product.id})">
                <div class="product-image-container">
                    <img src="${product.images[0]}" alt="${product.name}" class="product-image">
                    <div class="product-badges">
                        ${product.discount > 0 ? `<span class="badge bg-danger">-${product.discount}%</span>` : ''}
                        ${!product.inStock ? `<span class="badge bg-secondary">Hết hàng</span>` : ''}
                    </div>
                    <button class="fav-btn" onclick="event.stopPropagation(); toggleFavorite(${product.id})">
                        <i class="far fa-heart"></i>
                    </button>
                    ${product.images.length > 1 ? `
                        <button class="carousel-btn" onclick="event.stopPropagation(); showProductCarousel(${product.id})">
                            <i class="fas fa-images"></i>
                        </button>
                    ` : ''}
                </div>
                <div class="product-info">
                    <h6 class="product-name">${product.name}</h6>
                    <div class="product-price">
                        <span class="current-price">${this.formatPrice(product.price)}</span>
                        ${product.originalPrice > product.price ? `<span class="original-price">${this.formatPrice(product.originalPrice)}</span>` : ''}
                    </div>
                    <div class="product-rating">
                        <div class="stars">
                            ${this.renderStars(product.rating)}
                        </div>
                        <span class="rating-text">(${product.reviewCount})</span>
                    </div>
                    <div class="product-actions">
                        <button class="btn btn-primary btn-sm" onclick="event.stopPropagation(); addToCart(${product.id})">
                            <i class="fas fa-shopping-cart"></i> Thêm vào giỏ
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    renderStars(rating) {
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 !== 0;
        let stars = '';
        
        for (let i = 0; i < fullStars; i++) {
            stars += '<i class="fas fa-star"></i>';
        }
        
        if (hasHalfStar) {
            stars += '<i class="fas fa-star-half-alt"></i>';
        }
        
        const emptyStars = 5 - Math.ceil(rating);
        for (let i = 0; i < emptyStars; i++) {
            stars += '<i class="far fa-star"></i>';
        }
        
        return stars;
    }

    renderPagination() {
        const totalPages = Math.ceil(this.filteredProducts.length / this.itemsPerPage);
        const container = document.getElementById('pagination');
        
        if (totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        let paginationHTML = '';
        
        // Previous button
        paginationHTML += `
            <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${this.currentPage - 1})">Trước</a>
            </li>
        `;

        // Page numbers
        const startPage = Math.max(1, this.currentPage - 2);
        const endPage = Math.min(totalPages, this.currentPage + 2);

        if (startPage > 1) {
            paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="changePage(1)">1</a></li>`;
            if (startPage > 2) {
                paginationHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            paginationHTML += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="changePage(${i})">${i}</a>
                </li>
            `;
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                paginationHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="changePage(${totalPages})">${totalPages}</a></li>`;
        }

        // Next button
        paginationHTML += `
            <li class="page-item ${this.currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${this.currentPage + 1})">Sau</a>
            </li>
        `;

        container.innerHTML = paginationHTML;
    }

    applyFiltersAndSort() {
        let filtered = [...this.products];

        // Apply filters
        if (this.filters.brands.length > 0) {
            filtered = filtered.filter(p => this.filters.brands.includes(p.brand));
        }

        if (this.filters.colors.length > 0) {
            filtered = filtered.filter(p => this.filters.colors.includes(p.color));
        }

        if (this.filters.sizes.length > 0) {
            filtered = filtered.filter(p => p.size && p.size.some(s => this.filters.sizes.includes(s)));
        }

        if (this.filters.priceRange[0] > 0 || this.filters.priceRange[1] < 10000000) {
            filtered = filtered.filter(p => p.price >= this.filters.priceRange[0] && p.price <= this.filters.priceRange[1]);
        }

        // Apply sorting
        switch (this.sortBy) {
            case 'price-low':
                filtered.sort((a, b) => a.price - b.price);
                break;
            case 'price-high':
                filtered.sort((a, b) => b.price - a.price);
                break;
            case 'rating':
                filtered.sort((a, b) => b.rating - a.rating);
                break;
            case 'popular':
                filtered.sort((a, b) => b.reviewCount - a.reviewCount);
                break;
            case 'newest':
            default:
                filtered.sort((a, b) => b.id - a.id);
                break;
        }

        this.filteredProducts = filtered;
        this.currentPage = 1;
        this.updateProductCount();
        this.renderProducts();
        this.renderPagination();
    }

    updateProductCount() {
        const count = this.filteredProducts.length;
        document.getElementById('productCount').textContent = `${count} sản phẩm`;
        document.getElementById('resultCount').textContent = `${count} kết quả`;
    }

    updatePriceRange(value) {
        const maxPrice = parseInt(value);
        this.filters.priceRange[1] = maxPrice;
        document.getElementById('maxPrice').textContent = this.formatPrice(maxPrice);
        this.applyFiltersAndSort();
    }

    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    }

    formatSubcategoryName(subcategory) {
        const names = {
            'iphone': 'iPhone',
            'android': 'Android',
            'macbook': 'MacBook',
            'sneakers': 'Giày thể thao',
            'running': 'Giày chạy bộ'
        };
        return names[subcategory] || subcategory;
    }

    getColorValue(color) {
        const colors = {
            'Black': '#000000',
            'White': '#ffffff',
            'Red': '#ff0000',
            'Blue': '#0000ff',
            'Green': '#00ff00',
            'Titanium': '#c0c0c0'
        };
        return colors[color] || '#cccccc';
    }

    showLoading() {
        document.getElementById('productsContainer').innerHTML = `
            <div class="loading">
                <i class="fas fa-spinner fa-spin"></i> Đang tải sản phẩm...
            </div>
        `;
    }

    showError(message) {
        document.getElementById('productsContainer').innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚠️</div>
                <h3 class="empty-state-title">Lỗi</h3>
                <p class="empty-state-message">${message}</p>
            </div>
        `;
    }
}

// Global functions
let categoryManager;

document.addEventListener('DOMContentLoaded', function() {
    categoryManager = new CategoryManager();
});

function toggleView(view) {
    categoryManager.currentView = view;
    document.getElementById('gridView').classList.toggle('active', view === 'grid');
    document.getElementById('listView').classList.toggle('active', view === 'list');
    categoryManager.renderProducts();
}

function changePage(page) {
    categoryManager.currentPage = page;
    categoryManager.renderProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function toggleColorFilter(color) {
    const element = document.querySelector(`[data-color="${color}"]`);
    element.classList.toggle('selected');
    
    if (categoryManager.filters.colors.includes(color)) {
        categoryManager.filters.colors = categoryManager.filters.colors.filter(c => c !== color);
    } else {
        categoryManager.filters.colors.push(color);
    }
    
    categoryManager.applyFiltersAndSort();
}

function filterBySubcategory(subcategory) {
    categoryManager.filteredProducts = categoryManager.products.filter(p => p.subcategory === subcategory);
    categoryManager.currentPage = 1;
    categoryManager.updateProductCount();
    categoryManager.renderProducts();
    categoryManager.renderPagination();
}

function clearFilters() {
    categoryManager.filters = {
        priceRange: [0, 10000000],
        brands: [],
        sizes: [],
        colors: [],
        materials: [],
        genders: [],
        seasons: [],
        styles: [],
        fits: []
    };
    
    // Reset UI
    document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
    document.querySelectorAll('.color-option').forEach(el => el.classList.remove('selected'));
    document.getElementById('priceRange').value = 5000000;
    document.getElementById('maxPrice').textContent = '10,000,000đ';
    
    categoryManager.applyFiltersAndSort();
}

function viewProduct(productId) {
    window.location.href = `product-detail.html?id=${productId}`;
}

function toggleFavorite(productId) {
    // Implement favorite functionality
    console.log('Toggle favorite:', productId);
}

function addToCart(productId) {
    // Implement add to cart functionality
    console.log('Add to cart:', productId);
}

function showProductCarousel(productId) {
    const product = categoryManager.products.find(p => p.id === productId);
    if (product && product.images.length > 1) {
        const modal = new bootstrap.Modal(document.getElementById('productModal'));
        document.getElementById('productModalTitle').textContent = product.name;
        
        const carouselInner = document.getElementById('carouselInner');
        carouselInner.innerHTML = product.images.map((image, index) => `
            <div class="carousel-item ${index === 0 ? 'active' : ''}">
                <img src="${image}" class="d-block w-100" alt="${product.name}">
            </div>
        `).join('');
        
        modal.show();
    }
}

function toggleFavorite(productId) {
    // Toggle favorite status
    const btn = document.querySelector(`[onclick="toggleFavorite(${productId})"] i`);
    btn.classList.toggle('far');
    btn.classList.toggle('fas');
    btn.classList.toggle('text-danger');
}
