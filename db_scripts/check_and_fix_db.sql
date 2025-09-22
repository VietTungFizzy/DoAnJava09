-- Script kiểm tra và thêm các cột thiếu cho bảng users
USE marketplace_db2;

-- Kiểm tra cấu trúc bảng users hiện tại
DESCRIBE users;

-- Thêm các cột một cách an toàn (MySQL không hỗ trợ IF NOT EXISTS cho ADD COLUMN)
-- Sử dụng stored procedure để kiểm tra và thêm cột

DELIMITER $$

DROP PROCEDURE IF EXISTS AddColumnIfNotExists$$
CREATE PROCEDURE AddColumnIfNotExists(
    IN schemaName VARCHAR(64),
    IN tableName VARCHAR(64),
    IN columnName VARCHAR(64),
    IN columnDefinition TEXT
)
BEGIN
    DECLARE columnExists INT DEFAULT 0;

    SELECT COUNT(*) INTO columnExists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = schemaName
    AND TABLE_NAME = tableName
    AND COLUMN_NAME = columnName;

    IF columnExists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', schemaName, '.', tableName, ' ADD COLUMN ', columnName, ' ', columnDefinition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$

DELIMITER ;

-- Thêm các cột nếu chưa có
CALL AddColumnIfNotExists('marketplace_db2', 'users', 'failed_login_attempts', 'INT DEFAULT 0');
CALL AddColumnIfNotExists('marketplace_db2', 'users', 'locked_until', 'TIMESTAMP NULL');
CALL AddColumnIfNotExists('marketplace_db2', 'users', 'permanently_locked', 'BOOLEAN DEFAULT FALSE');

-- Xóa stored procedure sau khi sử dụng
DROP PROCEDURE IF EXISTS AddColumnIfNotExists;

-- Kiểm tra lại cấu trúc sau khi thêm
SELECT 'Cấu trúc bảng users sau khi cập nhật:' as info;
DESCRIBE users;

-- Kiểm tra xem có dữ liệu roles chưa
SELECT 'Kiểm tra roles:' as info;
SELECT * FROM roles;

-- Nếu chưa có roles thì thêm vào
INSERT IGNORE INTO roles (name) VALUES ('admin');
INSERT IGNORE INTO roles (name) VALUES ('seller');
INSERT IGNORE INTO roles (name) VALUES ('buyer');

SELECT 'Roles sau khi cập nhật:' as info;
SELECT * FROM roles;
