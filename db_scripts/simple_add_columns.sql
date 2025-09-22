-- Script đơn giản để thêm các cột thiếu (chạy từng lệnh một)
USE marketplace_db2;

-- Thêm từng cột một (nếu gặp lỗi "Duplicate column name" thì bỏ qua, có nghĩa là cột đã tồn tại)

-- Thêm cột failed_login_attempts
ALTER TABLE users ADD COLUMN failed_login_attempts INT DEFAULT 0;

-- Thêm cột locked_until
ALTER TABLE users ADD COLUMN locked_until TIMESTAMP NULL;

-- Thêm cột permanently_locked
ALTER TABLE users ADD COLUMN permanently_locked BOOLEAN DEFAULT FALSE;

-- Thêm roles nếu chưa có
INSERT IGNORE INTO roles (name) VALUES ('admin');
INSERT IGNORE INTO roles (name) VALUES ('seller');
INSERT IGNORE INTO roles (name) VALUES ('buyer');

-- Kiểm tra kết quả
DESCRIBE users;
SELECT * FROM roles;
