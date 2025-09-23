class AdminCategoryManager {
    constructor() {
        this.baseUrl = 'http://localhost:8080/api/categories';
        this.categories = [];
        this.editingCategory = null;
        
        this.init();
    }

    init() {
        this.loadCategories();
        this.setupEventListeners();
    }

    setupEventListeners() {
        document.getElementById('categoryForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleSubmit();
        });
    }

    async loadCategories() {
        try {
            const response = await fetch(this.baseUrl);
            if (response.ok) {
                this.categories = await response.json();
                this.renderCategories();
                this.updateParentSelect();
            } else {
                this.showAlert('Không thể tải danh sách danh mục', 'danger');
            }
        } catch (error) {
            console.error('Error loading categories:', error);
            this.showAlert('Lỗi kết nối đến server', 'danger');
        }
    }

    renderCategories() {
        const container = document.getElementById('categoriesList');
        if (this.categories.length === 0) {
            container.innerHTML = '<div class="text-center text-muted py-4">Chưa có danh mục nào</div>';
            return;
        }

        container.innerHTML = this.categories.map(category => `
            <div class="category-item">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <div class="d-flex align-items-center">
                            ${category.imageUrl ? `<img src="${category.imageUrl}" alt="${category.name}" class="me-3" style="width: 50px; height: 50px; object-fit: cover; border-radius: 5px;">` : ''}
                            <div>
                                <h5 class="mb-1">${category.name}</h5>
                                <p class="text-muted mb-1">${category.description || 'Không có mô tả'}</p>
                                <small class="text-muted">
                                    <i class="fas fa-sort"></i> Thứ tự: ${category.sortOrder || 0} | 
                                    <i class="fas fa-layer-group"></i> Danh mục con: ${category.subcategories ? category.subcategories.length : 0}
                                </small>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 text-end">
                        <button class="btn btn-sm btn-primary btn-edit" onclick="adminManager.editCategory(${category.id})">
                            <i class="fas fa-edit"></i> Sửa
                        </button>
                        <button class="btn btn-sm btn-danger btn-delete" onclick="adminManager.deleteCategory(${category.id})">
                            <i class="fas fa-trash"></i> Xóa
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    updateParentSelect() {
        const parentSelect = document.getElementById('parentId');
        const mainCategories = this.categories.filter(cat => !cat.parentId);
        
        parentSelect.innerHTML = '<option value="">Danh mục chính</option>' +
            mainCategories.map(cat => `<option value="${cat.id}">${cat.name}</option>`).join('');
    }

    async handleSubmit() {
        const formData = new FormData(document.getElementById('categoryForm'));
        const categoryData = Object.fromEntries(formData.entries());
        
        // Convert string values to appropriate types
        if (categoryData.sortOrder) {
            categoryData.sortOrder = parseInt(categoryData.sortOrder);
        }
        if (categoryData.parentId === '') {
            categoryData.parentId = null;
        } else if (categoryData.parentId) {
            categoryData.parentId = parseInt(categoryData.parentId);
        }

        try {
            let response;
            if (this.editingCategory) {
                // Update existing category
                response = await fetch(`${this.baseUrl}/${this.editingCategory}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(categoryData)
                });
            } else {
                // Create new category
                response = await fetch(this.baseUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(categoryData)
                });
            }

            if (response.ok) {
                this.showAlert(
                    this.editingCategory ? 'Cập nhật danh mục thành công!' : 'Thêm danh mục thành công!', 
                    'success'
                );
                this.resetForm();
                this.loadCategories();
            } else {
                this.showAlert('Có lỗi xảy ra khi lưu danh mục', 'danger');
            }
        } catch (error) {
            console.error('Error saving category:', error);
            this.showAlert('Lỗi kết nối đến server', 'danger');
        }
    }

    editCategory(id) {
        const category = this.categories.find(cat => cat.id === id);
        if (!category) return;

        this.editingCategory = id;
        document.getElementById('categoryId').value = category.id;
        document.getElementById('categoryName').value = category.name;
        document.getElementById('description').value = category.description || '';
        document.getElementById('imageUrl').value = category.imageUrl || '';
        document.getElementById('iconUrl').value = category.iconUrl || '';
        document.getElementById('bannerUrl').value = category.bannerUrl || '';
        document.getElementById('sortOrder').value = category.sortOrder || 0;
        document.getElementById('parentId').value = category.parentId || '';

        // Scroll to form
        document.querySelector('.category-form').scrollIntoView({ behavior: 'smooth' });
    }

    async deleteCategory(id) {
        if (!confirm('Bạn có chắc chắn muốn xóa danh mục này?')) {
            return;
        }

        try {
            const response = await fetch(`${this.baseUrl}/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                this.showAlert('Xóa danh mục thành công!', 'success');
                this.loadCategories();
            } else {
                this.showAlert('Có lỗi xảy ra khi xóa danh mục', 'danger');
            }
        } catch (error) {
            console.error('Error deleting category:', error);
            this.showAlert('Lỗi kết nối đến server', 'danger');
        }
    }

    resetForm() {
        this.editingCategory = null;
        document.getElementById('categoryForm').reset();
        document.getElementById('categoryId').value = '';
    }

    showAlert(message, type) {
        const alertContainer = document.getElementById('alertContainer');
        alertContainer.innerHTML = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
        
        // Auto hide after 5 seconds
        setTimeout(() => {
            const alert = alertContainer.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 5000);
    }
}

// Initialize admin manager when page loads
let adminManager;
document.addEventListener('DOMContentLoaded', () => {
    adminManager = new AdminCategoryManager();
});

// Global functions for onclick handlers
function resetForm() {
    adminManager.resetForm();
}

function loadCategories() {
    adminManager.loadCategories();
}
