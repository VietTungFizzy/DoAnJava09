// Categories Page JavaScript for Clothing Store
class CategoriesManager {
    constructor() {
        this.categories = [];
        this.init();
    }

    init() {
        this.loadCategories();
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', (e) => {
            this.handleSearch(e.target.value);
        });
    }

    async loadCategories() {
        try {
            this.showLoading();
            
            // Simulate API call - replace with actual API endpoint
            const response = await this.fetchCategories();
            this.categories = response.categories || [];
            
            this.updateCategoryCount();
            this.renderCategories();
            
        } catch (error) {
            console.error('Error loading categories:', error);
            this.showError('Không thể tải danh mục. Vui lòng thử lại.');
        }
    }

    async fetchCategories() {
        // Mock data for clothing categories
        return {
            categories: [
                {
                    id: 1,
                    name: 'Quần áo nam',
                    description: 'Thời trang nam với phong cách hiện đại và trẻ trung',
                    imageUrl: 'images/category/men-fashion.jpg',
                    iconUrl: 'images/icons-product/tshirt.svg',
                    productCount: 0,
                    subcategories: [
                        { id: 11, name: 'Áo thun', productCount: 450 },
                        { id: 12, name: 'Áo sơ mi', productCount: 320 },
                        { id: 13, name: 'Quần jeans', productCount: 280 },
                        { id: 14, name: 'Quần short', productCount: 200 }
                    ]
                },
                {
                    id: 2,
                    name: 'Quần áo nữ',
                    description: 'Thời trang nữ đa dạng từ casual đến formal',
                    imageUrl: 'images/category/women-fashion.jpg',
                    iconUrl: 'images/icons-product/wear.svg',
                    productCount: 0,
                    subcategories: [
                        { id: 21, name: 'Váy', productCount: 650 },
                        { id: 22, name: 'Áo blouse', productCount: 480 },
                        { id: 23, name: 'Quần jeans', productCount: 420 },
                        { id: 24, name: 'Áo khoác', productCount: 350 },
                        { id: 25, name: 'Đầm', productCount: 200 }
                    ]
                },
                {
                    id: 3,
                    name: 'Giày dép',
                    description: 'Giày dép nam nữ với chất lượng cao',
                    imageUrl: 'images/category/shoes.jpg',
                    iconUrl: 'images/icons-product/shoes.svg',
                    productCount: 0,
                    subcategories: [
                        { id: 31, name: 'Giày thể thao', productCount: 350 },
                        { id: 32, name: 'Giày tây', productCount: 200 },
                        { id: 33, name: 'Giày cao gót', productCount: 180 },
                        { id: 34, name: 'Sandal', productCount: 120 }
                    ]
                },
                {
                    id: 4,
                    name: 'Phụ kiện',
                    description: 'Phụ kiện thời trang đa dạng',
                    imageUrl: 'images/category/accessories.jpg',
                    iconUrl: 'images/icons-product/bags.svg',
                    productCount: 420,
                    subcategories: [
                        { id: 41, name: 'Túi xách', productCount: 150 },
                        { id: 42, name: 'Ví', productCount: 120 },
                        { id: 43, name: 'Đồng hồ', productCount: 80 },
                        { id: 44, name: 'Trang sức', productCount: 70 }
                    ]
                },
                {
                    id: 5,
                    name: 'Trẻ em',
                    description: 'Thời trang trẻ em an toàn và thoải mái',
                    imageUrl: 'images/category/kids-fashion.jpg',
                    iconUrl: 'images/icons-product/kid.svg',
                    productCount: 680,
                    subcategories: [
                        { id: 51, name: 'Quần áo bé trai', productCount: 250 },
                        { id: 52, name: 'Quần áo bé gái', productCount: 280 },
                        { id: 53, name: 'Giày dép trẻ em', productCount: 150 }
                    ]
                },
                {
                    id: 6,
                    name: 'Thể thao',
                    description: 'Trang phục và phụ kiện thể thao',
                    imageUrl: 'images/category/sports.jpg',
                    iconUrl: 'images/icons-product/ball.svg',
                    productCount: 320,
                    subcategories: [
                        { id: 61, name: 'Đồ tập gym', productCount: 120 },
                        { id: 62, name: 'Đồ chạy bộ', productCount: 100 },
                        { id: 63, name: 'Đồ bơi', productCount: 50 },
                        { id: 64, name: 'Phụ kiện thể thao', productCount: 50 }
                    ]
                }
            ]
        };
    }

    renderCategories() {
        const container = document.getElementById('categoriesContainer');
        
        if (this.categories.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">📂</div>
                    <h3 class="empty-state-title">Không có danh mục</h3>
                    <p class="empty-state-message">Hiện tại chưa có danh mục sản phẩm nào</p>
                </div>
            `;
            return;
        }

        container.innerHTML = this.categories.map(category => this.renderCategoryCard(category)).join('');
    }

    renderCategoryCard(category) {
        return `
            <div class="col-lg-4 col-md-6 mb-4">
                <div class="category-card" onclick="viewCategory(${category.id})">
                    <div class="category-image-container">
                        <img src="${category.imageUrl}" alt="${category.name}" class="category-image">
                        <div class="category-overlay">
                            <div class="category-icon">
                                <img src="${category.iconUrl}" alt="${category.name}">
                            </div>
                        </div>
                    </div>
                    <div class="category-info">
                        <h5 class="category-name">${category.name}</h5>
                        <p class="category-description">${category.description}</p>
                        <div class="category-stats">
                            <span class="subcategory-count">${category.subcategories.length} danh mục con</span>
                        </div>
                        <div class="subcategories-preview">
                            ${category.subcategories.slice(0, 3).map(sub => `
                                <span class="subcategory-tag">${sub.name}</span>
                            `).join('')}
                            ${category.subcategories.length > 3 ? `<span class="subcategory-more">+${category.subcategories.length - 3} khác</span>` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    updateCategoryCount() {
        const count = this.categories.length;
        document.getElementById('categoryCount').textContent = `${count} danh mục`;
    }

    handleSearch(keyword) {
        if (!keyword.trim()) {
            this.renderCategories();
            return;
        }

        const filteredCategories = this.categories.filter(category => 
            category.name.toLowerCase().includes(keyword.toLowerCase()) ||
            category.description.toLowerCase().includes(keyword.toLowerCase())
        );

        const container = document.getElementById('categoriesContainer');
        if (filteredCategories.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">🔍</div>
                    <h3 class="empty-state-title">Không tìm thấy danh mục</h3>
                    <p class="empty-state-message">Hãy thử từ khóa khác</p>
                </div>
            `;
            return;
        }

        container.innerHTML = filteredCategories.map(category => this.renderCategoryCard(category)).join('');
    }

    showLoading() {
        document.getElementById('categoriesContainer').innerHTML = `
            <div class="loading">
                <i class="fas fa-spinner fa-spin"></i> Đang tải danh mục...
            </div>
        `;
    }

    showError(message) {
        document.getElementById('categoriesContainer').innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚠️</div>
                <h3 class="empty-state-title">Lỗi</h3>
                <p class="empty-state-message">${message}</p>
            </div>
        `;
    }
}

// Global functions
let categoriesManager;

document.addEventListener('DOMContentLoaded', function() {
    categoriesManager = new CategoriesManager();
});

function viewCategory(categoryId) {
    // Navigate to category page with products
    window.location.href = `category-page.html?category=${categoryId}`;
}