-- Script setup dữ liệu cần thiết để chạy ứng dụng
USE marketplace_db2;

-- 1. Thêm các cột thiếu cho bảng users (nếu chưa có)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS failed_login_attempts INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS permanently_locked BOOLEAN DEFAULT FALSE;

-- 2. Insert các roles cần thiết
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'admin'),
(2, 'seller'),
(3, 'buyer');

-- 3. Tạo user admin mặc định (password: admin123)
-- Password được encode bằng BCrypt: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
INSERT IGNORE INTO users (name, email, password, phone, role_id, status, failed_login_attempts, permanently_locked)
VALUES ('Administrator', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '0123456789', 1, 'active', 0, FALSE);

-- 4. Tạo một số categories mẫu
INSERT IGNORE INTO categories (id, name, slug, parent_id, path, depth) VALUES
(1, 'Điện tử', 'dien-tu', NULL, '/dien-tu', 0),
(2, 'Thời trang', 'thoi-trang', NULL, '/thoi-trang', 0),
(3, 'Gia dụng', 'gia-dung', NULL, '/gia-dung', 0),
(4, 'Sách', 'sach', NULL, '/sach', 0),
(5, 'Thể thao', 'the-thao', NULL, '/the-thao', 0);

-- 5. Tạo một số brands mẫu
INSERT IGNORE INTO brands (id, name, slug) VALUES
(1, 'Samsung', 'samsung'),
(2, 'Apple', 'apple'),
(3, 'Nike', 'nike'),
(4, 'Adidas', 'adidas'),
(5, 'Uniqlo', 'uniqlo');

-- Kiểm tra dữ liệu đã insert
SELECT 'Roles:' as info;
SELECT * FROM roles;

SELECT 'Users:' as info;
SELECT id, name, email, role_id, status FROM users;

SELECT 'Categories:' as info;
SELECT * FROM categories;

SELECT 'Brands:' as info;
SELECT * FROM brands;
