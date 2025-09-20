-- Tạo database
CREATE DATABASE marketplace_db2;
USE marketplace_db2;

-- ================== USERS & ROLES ==================
CREATE TABLE roles (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) UNIQUE                 -- 'admin','seller','buyer'
);

CREATE TABLE users (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(255),
  email        VARCHAR(255) UNIQUE,
  password     VARCHAR(255),
  phone        VARCHAR(20),
  role_id      INT NOT NULL,
  status       ENUM('active','inactive') DEFAULT 'active',
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at   TIMESTAMP NULL,
  FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE user_addresses (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  user_id       INT NOT NULL,
  type          ENUM('shipping','billing') NOT NULL DEFAULT 'shipping',
  label         VARCHAR(100),
  full_name     VARCHAR(255),
  phone         VARCHAR(20),
  address_line1 VARCHAR(255) NOT NULL,
  address_line2 VARCHAR(255),
  ward          VARCHAR(100),
  district      VARCHAR(100),
  city          VARCHAR(100),
  country_code  CHAR(2) NOT NULL DEFAULT 'VN',
  postal_code   VARCHAR(20),
  is_default    BOOLEAN NOT NULL DEFAULT FALSE,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ================== STORES (MULTI-SELLER) ==================
CREATE TABLE stores (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  user_id         INT NOT NULL,               -- owner
  name            VARCHAR(255) NOT NULL,
  slug            VARCHAR(255) UNIQUE,
  description     TEXT,
  address         TEXT,
  status          ENUM('active','inactive','suspended') DEFAULT 'active',
  commission_rate DECIMAL(5,2) DEFAULT 10.00, -- % sàn thu
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE store_users (
  store_id INT,
  user_id  INT,
  role     ENUM('owner','manager','staff') DEFAULT 'staff',
  PRIMARY KEY (store_id, user_id),
  FOREIGN KEY (store_id) REFERENCES stores(id),
  FOREIGN KEY (user_id)  REFERENCES users(id)
);

-- ================== CATALOG ==================
CREATE TABLE brands (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) UNIQUE,
  slug VARCHAR(150) UNIQUE
);

CREATE TABLE categories (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  slug      VARCHAR(255) UNIQUE,
  parent_id INT NULL,
  path      VARCHAR(1000),
  depth     INT DEFAULT 0,
  FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE products (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  store_id    INT NOT NULL,
  name        VARCHAR(255) NOT NULL,
  slug        VARCHAR(255) UNIQUE,
  brand_id    INT NULL,
  description LONGTEXT,
  status      ENUM('draft','active','inactive') DEFAULT 'active',
  visibility  ENUM('public','hidden') DEFAULT 'public',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at  TIMESTAMP NULL,
  FOREIGN KEY (store_id) REFERENCES stores(id),
  FOREIGN KEY (brand_id) REFERENCES brands(id)
);

CREATE TABLE product_categories (
  product_id  INT,
  category_id INT,
  PRIMARY KEY (product_id, category_id),
  FOREIGN KEY (product_id)  REFERENCES products(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE product_options (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  name       VARCHAR(100) NOT NULL,   -- Size / Color...
  position   INT DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE product_option_values (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  option_id INT NOT NULL,
  value     VARCHAR(150) NOT NULL,    -- 'S','M','L' hoặc 'Red'
  position  INT DEFAULT 0,
  FOREIGN KEY (option_id) REFERENCES product_options(id)
);

CREATE TABLE skus (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  product_id       INT NOT NULL,
  sku_code         VARCHAR(100) UNIQUE,
  barcode          VARCHAR(100),
  price            DECIMAL(12,2) NOT NULL,
  compare_at_price DECIMAL(12,2) NULL,
  cost_price       DECIMAL(12,2) NULL,
  weight_gram      INT DEFAULT 0,
  length_mm        INT DEFAULT 0,
  width_mm         INT DEFAULT 0,
  height_mm        INT DEFAULT 0,
  status           ENUM('active','inactive') DEFAULT 'active',
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE sku_attributes (
  sku_id    INT,
  option_id INT,
  value_id  INT,
  PRIMARY KEY (sku_id, option_id),
  FOREIGN KEY (sku_id)    REFERENCES skus(id),
  FOREIGN KEY (option_id) REFERENCES product_options(id),
  FOREIGN KEY (value_id)  REFERENCES product_option_values(id)
);

CREATE TABLE product_images (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  image_url  VARCHAR(500) NOT NULL,
  position   INT DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE product_videos (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  product_id  INT NOT NULL,
  video_url   VARCHAR(500) NOT NULL,   -- link Youtube, Vimeo, hoặc CDN
  thumbnail   VARCHAR(500) NULL,       -- ảnh preview (optional)
  title       VARCHAR(255) NULL,
  position    INT DEFAULT 0,           -- thứ tự hiển thị
  is_active   BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE sku_images (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  sku_id    INT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  position  INT DEFAULT 0,
  FOREIGN KEY (sku_id) REFERENCES skus(id)
);

CREATE TABLE inventories (
  sku_id   INT PRIMARY KEY,
  quantity INT DEFAULT 0,
  reserved INT DEFAULT 0,
  FOREIGN KEY (sku_id)   REFERENCES skus(id)
);

-- ================== VOUCHERS ==================
CREATE TABLE vouchers (
  id                   INT AUTO_INCREMENT PRIMARY KEY,
  code                 VARCHAR(50) UNIQUE,
  type                 ENUM('fixed','percent') NOT NULL,
  value                DECIMAL(12,2) NOT NULL,      -- percent: 5 = 5%
  max_discount         DECIMAL(12,2) NULL,
  min_order_total      DECIMAL(12,2) NULL,
  store_id             INT NULL,                    -- NULL = toàn sàn
  start_date           DATETIME,
  end_date             DATETIME,
  usage_limit          INT NULL,
  usage_limit_per_user INT NULL,
  status               ENUM('active','inactive','expired') DEFAULT 'active',
  created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- ================== CART & WISHLIST ==================
CREATE TABLE carts (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  user_id     INT NULL,
  session_key VARCHAR(100) NULL,
  currency    CHAR(3) DEFAULT 'VND',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  cart_id        INT NOT NULL,
  sku_id         INT NOT NULL,
  quantity       INT NOT NULL,
  price_snapshot DECIMAL(12,2) NULL,
  FOREIGN KEY (cart_id) REFERENCES carts(id),
  FOREIGN KEY (sku_id)  REFERENCES skus(id)
);

CREATE TABLE wishlists (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  name       VARCHAR(100) DEFAULT 'Default',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, name),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE wishlist_items (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  wishlist_id  INT NOT NULL,
  sku_id       INT NOT NULL,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (wishlist_id, sku_id),
  FOREIGN KEY (wishlist_id) REFERENCES wishlists(id),
  FOREIGN KEY (sku_id)      REFERENCES skus(id)
);

-- ================== ORDERS / PAYMENTS / SHIPMENTS ==================
CREATE TABLE orders (
  id                  INT AUTO_INCREMENT PRIMARY KEY,
  order_number        VARCHAR(30) UNIQUE,
  user_id             INT NOT NULL,
  currency            CHAR(3) DEFAULT 'VND',
  order_status        ENUM('pending','confirmed','cancelled','completed','closed') DEFAULT 'pending',
  payment_status      ENUM('unpaid','paid','refunded','partial_refund') DEFAULT 'unpaid',
  fulfillment_status  ENUM('unfulfilled','partially_fulfilled','fulfilled') DEFAULT 'unfulfilled',
  subtotal            DECIMAL(12,2) DEFAULT 0,
  discount_total      DECIMAL(12,2) DEFAULT 0,
  shipping_total      DECIMAL(12,2) DEFAULT 0,
  tax_total           DECIMAL(12,2) DEFAULT 0,
  total               DECIMAL(12,2) DEFAULT 0,
  voucher_id          INT NULL,
  placed_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)    REFERENCES users(id),
  FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
);

CREATE TABLE order_items (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  order_id       INT NOT NULL,
  store_id       INT NOT NULL,
  sku_id         INT NOT NULL,
  product_name   VARCHAR(255),
  sku_attributes JSON NULL,
  quantity       INT NOT NULL,
  unit_price     DECIMAL(12,2) NOT NULL,
  discount       DECIMAL(12,2) DEFAULT 0,
  tax            DECIMAL(12,2) DEFAULT 0,
  total          DECIMAL(12,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (store_id) REFERENCES stores(id),
  FOREIGN KEY (sku_id)   REFERENCES skus(id)
);

CREATE TABLE payment_transactions (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  order_id      INT NOT NULL,
  user_id       INT NOT NULL,
  provider      VARCHAR(50),
  method        VARCHAR(50),
  amount        DECIMAL(12,2) NOT NULL,
  currency      CHAR(3) DEFAULT 'VND',
  status        ENUM('pending','authorized','captured','failed','refunded') DEFAULT 'pending',
  provider_ref  VARCHAR(150),
  message       VARCHAR(500),
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (user_id)  REFERENCES users(id)
);

CREATE TABLE refunds (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  order_id       INT NOT NULL,
  transaction_id INT NULL,
  amount         DECIMAL(12,2) NOT NULL,
  reason         VARCHAR(255),
  status         ENUM('requested','approved','rejected','processed') DEFAULT 'requested',
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (transaction_id) REFERENCES payment_transactions(id)
);

CREATE TABLE shipments (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  order_id        INT NOT NULL,
  store_id        INT NOT NULL,
  carrier         VARCHAR(100),
  tracking_number VARCHAR(100),
  status          ENUM('pending','shipped','delivered','returned','cancelled') DEFAULT 'pending',
  shipped_at      DATETIME NULL,
  delivered_at    DATETIME NULL,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE shipment_items (
  shipment_id   INT,
  order_item_id INT,
  quantity      INT NOT NULL,
  PRIMARY KEY (shipment_id, order_item_id),
  FOREIGN KEY (shipment_id)  REFERENCES shipments(id),
  FOREIGN KEY (order_item_id) REFERENCES order_items(id)
);

-- ================== REVIEWS ==================
CREATE TABLE reviews (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  user_id       INT NOT NULL,
  product_id    INT NOT NULL,
  sku_id        INT NULL,
  order_item_id INT NULL,
  rating        TINYINT NOT NULL,
  title         VARCHAR(150) NULL,
  comment       TEXT,
  status        ENUM('pending','approved','rejected') DEFAULT 'pending',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)    REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES products(id),
  FOREIGN KEY (sku_id)     REFERENCES skus(id),
  FOREIGN KEY (order_item_id) REFERENCES order_items(id)
);

CREATE TABLE store_reviews (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  store_id   INT NOT NULL,
  rating     TINYINT NOT NULL,
  comment    TEXT,
  status     ENUM('pending','approved','rejected') DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)  REFERENCES users(id),
  FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- ================== SUPPORT & AUDIT ==================
CREATE TABLE support_tickets (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  store_id   INT NULL,
  subject    VARCHAR(255),
  content    TEXT,
  status     ENUM('open','closed','pending') DEFAULT 'open',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)  REFERENCES users(id),
  FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE TABLE audit_logs (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NULL,
  entity     VARCHAR(100),
  entity_id  VARCHAR(64),
  action     VARCHAR(50),
  metadata   JSON NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_entity (entity, entity_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);