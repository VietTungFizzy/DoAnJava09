-- Seed data for marketplace_db2
-- Run after creating the schema in Script.sql

-- =============== ROLES ===============
INSERT INTO roles (id, name) VALUES
(1, 'admin'),
(2, 'seller'),
(3, 'buyer');

-- =============== USERS ===============
INSERT INTO users (id, name, email, password, phone, role_id, status, failed_login_attempts, permanently_locked)
VALUES
(1, 'Platform Admin', 'admin@example.com', '$2y$10$adminhash', '0123456789', 1, 'active', 0, FALSE),
(2, 'Seller One',      'seller@example.com', '$2y$10$sellerhash', '0987654321', 2, 'active', 0, FALSE),
(3, 'Buyer One',       'buyer@example.com',  '$2y$10$buyerhash',  '0912345678', 3, 'active', 0, FALSE),
(4, 'Store Manager',   'manager@example.com','$2y$10$managerhash','0901122334', 2, 'active', 0, FALSE);

-- =============== USER ADDRESSES ===============
INSERT INTO user_addresses (user_id, type, label, full_name, phone, address_line1, address_line2, ward, district, city, country_code, postal_code, is_default)
VALUES
(3, 'shipping', 'Home', 'Buyer One', '0912345678', '123 Le Loi', 'Apt 12', 'Ben Nghe', 'District 1', 'Ho Chi Minh', 'VN', '700000', TRUE),
(2, 'shipping', 'Store HQ', 'Seller One', '0987654321', '45 Nguyen Trai', NULL, 'Ward 7', 'District 5', 'Ho Chi Minh', 'VN', '700000', TRUE);

-- =============== STORES ===============
INSERT INTO stores (id, user_id, name, slug, description, address, status, commission_rate)
VALUES
(1, 2, 'Seller One Store', 'seller-one-store', 'Official store of Seller One', '45 Nguyen Trai, District 5, HCM', 'active', 10.00);

INSERT INTO store_users (store_id, user_id, role)
VALUES
(1, 2, 'owner'),
(1, 4, 'manager');

-- =============== BRANDS ===============
INSERT INTO brands (id, name, slug) VALUES
(1, 'Nike', 'nike'),
(2, 'Samsung', 'samsung');

-- =============== CATEGORIES ===============
INSERT INTO categories (id, name, slug, parent_id, path, depth) VALUES
(1, 'Electronics', 'electronics', NULL, '/electronics', 0),
(2, 'Smartphones', 'smartphones', 1, '/electronics/smartphones', 1),
(3, 'Clothing', 'clothing', NULL, '/clothing', 0),
(4, 'Men', 'men', 3, '/clothing/men', 1);

-- =============== PRODUCTS ===============
INSERT INTO products (id, store_id, name, slug, brand_id, description, status, visibility)
VALUES
(1, 1, 'Galaxy S21', 'galaxy-s21', 2, 'Samsung Galaxy S21 - powerful performance and pro-grade camera.', 'active', 'public'),
(2, 1, 'Basic T-Shirt', 'basic-tshirt', 1, 'Comfortable cotton t-shirt.', 'active', 'public');

INSERT INTO product_categories (product_id, category_id) VALUES
(1, 2),
(2, 4);

-- =============== PRODUCT OPTIONS & VALUES ===============
-- Product 1 (Galaxy S21) options: Color, Storage
INSERT INTO product_options (id, product_id, name, position) VALUES
(1, 1, 'Color', 1),
(2, 1, 'Storage', 2);

INSERT INTO product_option_values (id, option_id, value, position) VALUES
(1, 1, 'Black', 1),
(2, 1, 'White', 2),
(3, 2, '128GB', 1),
(4, 2, '256GB', 2);

-- Product 2 (Basic T-Shirt) option: Size
INSERT INTO product_options (id, product_id, name, position) VALUES
(3, 2, 'Size', 1);

INSERT INTO product_option_values (id, option_id, value, position) VALUES
(5, 3, 'S', 1),
(6, 3, 'M', 2),
(7, 3, 'L', 3);

-- =============== SKUS ===============
INSERT INTO skus (id, product_id, sku_code, barcode, price, compare_at_price, cost_price, weight_gram, status)
VALUES
(1, 1, 'SS21-BLK-128', '8800000000001', 15000000.00, 16500000.00, 12000000.00, 169, 'active'),
(2, 1, 'SS21-WHT-256', '8800000000002', 17000000.00, 18500000.00, 14000000.00, 169, 'active'),
(3, 2, 'TSHIRT-S', '0000000000010', 250000.00, NULL, 120000.00, 150, 'active'),
(4, 2, 'TSHIRT-M', '0000000000011', 250000.00, NULL, 120000.00, 150, 'active'),
(5, 2, 'TSHIRT-L', '0000000000012', 250000.00, NULL, 120000.00, 150, 'active');

-- =============== SKU ATTRIBUTES (link skus to option values) ===============
-- SKU 1: Black, 128GB
INSERT INTO sku_attributes (sku_id, option_id, value_id) VALUES
(1, 1, 1),
(1, 2, 3);

-- SKU 2: White, 256GB
INSERT INTO sku_attributes (sku_id, option_id, value_id) VALUES
(2, 1, 2),
(2, 2, 4);

-- T-Shirt SKUs
INSERT INTO sku_attributes (sku_id, option_id, value_id) VALUES
(3, 3, 5),
(4, 3, 6),
(5, 3, 7);

-- =============== INVENTORIES ===============
INSERT INTO inventories (sku_id, quantity, reserved) VALUES
(1, 10, 1),
(2, 5, 0),
(3, 20, 0),
(4, 15, 0),
(5, 8, 0);

-- =============== PRODUCT IMAGES & SKU IMAGES ===============
INSERT INTO product_images (product_id, image_url, position) VALUES
(1, 'https://cdn.example.com/products/galaxy-s21-1.jpg', 1),
(1, 'https://cdn.example.com/products/galaxy-s21-2.jpg', 2),
(2, 'https://cdn.example.com/products/basic-tshirt-1.jpg', 1);

INSERT INTO sku_images (sku_id, image_url, position) VALUES
(1, 'https://cdn.example.com/skus/ss21-black.jpg', 1),
(2, 'https://cdn.example.com/skus/ss21-white.jpg', 1),
(3, 'https://cdn.example.com/skus/tshirt-s.jpg', 1);

-- =============== PRODUCT VIDEOS ===============
INSERT INTO product_videos (product_id, video_url, thumbnail, title, position, is_active)
VALUES
(1, 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 'https://cdn.example.com/products/galaxy-s21-thumb.jpg', 'Galaxy S21 Overview', 1, TRUE);

-- =============== VOUCHERS ===============
INSERT INTO vouchers (id, code, type, value, max_discount, min_order_total, store_id, start_date, end_date, usage_limit, usage_limit_per_user, status)
VALUES
(1, 'WELCOME10', 'percent', 10.00, NULL, 100000.00, NULL, '2026-01-01 00:00:00', '2026-12-31 23:59:59', NULL, 1, 'active'),
(2, 'STORE50K', 'fixed', 50000.00, NULL, 300000.00, 1, '2026-01-01 00:00:00', '2026-06-30 23:59:59', 100, 1, 'active');

-- =============== CARTS & CART ITEMS ===============
INSERT INTO carts (id, user_id, session_key, currency) VALUES
(1, 3, NULL, 'VND');

INSERT INTO cart_items (cart_id, sku_id, quantity, price_snapshot) VALUES
(1, 1, 1, 15000000.00),  -- Buyer has Galaxy S21 in cart
(1, 3, 2, 250000.00);    -- and two T-Shirts size S

-- =============== WISHLISTS & ITEMS ===============
INSERT INTO wishlists (id, user_id, name) VALUES
(1, 3, 'Default');

INSERT INTO wishlist_items (wishlist_id, sku_id) VALUES
(1, 2); -- Buyer added SS21 white 256GB to wishlist

-- =============== ORDERS & ORDER ITEMS ===============
-- Example order using voucher WELCOME10
INSERT INTO orders (id, order_number, user_id, currency, order_status, payment_status, fulfillment_status, subtotal, discount_total, shipping_total, tax_total, total, voucher_id, placed_at)
VALUES
(1, 'ORD-20260120-0001', 3, 'VND', 'confirmed', 'paid', 'fulfilled', 15000000.00, 1500000.00, 30000.00, 0.00, 13530000.00, 1, NOW());

-- order_items: single line for sku 1
INSERT INTO order_items (id, order_id, store_id, sku_id, product_name, sku_attributes, quantity, unit_price, discount, tax, total)
VALUES
(1, 1, 1, 1, 'Galaxy S21', '{"Color":"Black","Storage":"128GB"}', 1, 15000000.00, 1500000.00, 0.00, 13500000.00);

-- =============== PAYMENT TRANSACTIONS ===============
INSERT INTO payment_transactions (id, order_id, user_id, provider, method, amount, currency, status, provider_ref, message)
VALUES
(1, 1, 3, 'MockPay', 'card', 13530000.00, 'VND', 'captured', 'MPAY-12345', 'Payment captured');

-- =============== SHIPMENTS & SHIPMENT ITEMS ===============
INSERT INTO shipments (id, order_id, store_id, carrier, tracking_number, status, shipped_at, created_at)
VALUES
(1, 1, 1, 'Viettel Post', 'VT123456789', 'shipped', NOW(), NOW());

INSERT INTO shipment_items (shipment_id, order_item_id, quantity) VALUES
(1, 1, 1);

-- =============== REVIEWS & STORE REVIEWS ===============
INSERT INTO reviews (id, user_id, product_id, sku_id, order_item_id, rating, title, comment, status)
VALUES
(1, 3, 1, 1, 1, 5, 'Excellent phone', 'Battery and camera are great. Fast delivery.', 'approved');

INSERT INTO store_reviews (id, user_id, store_id, rating, comment, status)
VALUES
(1, 3, 1, 5, 'Fast shipping and good packaging.', 'approved');

-- =============== SUPPORT TICKETS ===============
INSERT INTO support_tickets (id, user_id, store_id, subject, content, status)
VALUES
(1, 3, 1, 'Missing charger in package', 'I received the phone but the charger is missing from the box. Please advise.', 'open');

-- =============== AUDIT LOGS ===============
INSERT INTO audit_logs (user_id, entity, entity_id, action, metadata)
VALUES
(3, 'orders', '1', 'created', JSON_OBJECT('order_number','ORD-20260120-0001','subtotal',15000000));

-- =============== SAMPLE REFUND (optional) ===============
-- Example of a small refund (commented out). Uncomment to use.
-- INSERT INTO refunds (order_id, transaction_id, amount, reason, status) VALUES
-- (1, 1, 500000.00, 'Partial refund for accessory', 'processed');

-- =============== ADDITIONAL DATA ===============
-- Insert at least 10 products per category (based on Script.sql schema)
-- Assumes roles, users, stores, brands, and categories from initial schema/seed already exist.
-- Existing product ids 1 and 2 were used in prior seed; new products use ids 3..42.
-- Each product will have one SKU (skus ids 6..45) and an inventory row.

-- =============== PRODUCTS: Electronics (category_id = 1) - ids 3..12 ===============
INSERT INTO products (id, store_id, name, slug, brand_id, description, status, visibility) VALUES
(3,  1, 'Wireless Headphones',    'wireless-headphones-3', NULL, 'Over-ear wireless headphones with noise cancellation.', 'active', 'public'),
(4,  1, 'Bluetooth Speaker',      'bluetooth-speaker-4',   NULL, 'Portable Bluetooth speaker with long battery life.', 'active', 'public'),
(5,  1, 'Smartwatch X',           'smartwatch-x-5',        NULL, 'Smartwatch with fitness tracking and AMOLED display.', 'active', 'public'),
(6,  1, '4K Ultra HD TV 55"',     '4k-ultra-hd-tv-55-6',   NULL, '55-inch 4K Ultra HD smart TV with HDR.', 'active', 'public'),
(7,  1, 'Laptop Pro 14"',         'laptop-pro-14-7',       NULL, '14-inch professional laptop with M-series style CPU.', 'active', 'public'),
(8,  1, 'Gaming Console Z',       'gaming-console-z-8',    NULL, 'Next-gen gaming console with 4K support.', 'active', 'public'),
(9,  1, 'USB-C Fast Charger 65W','usb-c-fast-charger-9',  NULL, '65W PD fast charger for laptops and phones.', 'active', 'public'),
(10, 1, 'External SSD 1TB',       'external-ssd-1tb-10',   NULL, 'Portable NVMe external SSD, 1TB.', 'active', 'public'),
(11, 1, 'WiFi 6 Router',          'wifi6-router-11',       NULL, 'Dual-band WiFi 6 router with mesh support.', 'active', 'public'),
(12, 1, 'Action Camera 4K',       'action-camera-4k-12',   NULL, 'Waterproof action camera capable of 4K/60fps.', 'active', 'public');

-- Map Electronics products to category_id = 1
INSERT INTO product_categories (product_id, category_id) VALUES
(3, 1),(4, 1),(5, 1),(6, 1),(7, 1),(8, 1),(9, 1),(10, 1),(11, 1),(12, 1);

-- =============== PRODUCTS: Smartphones (category_id = 2) - ids 13..22 ===============
INSERT INTO products (id, store_id, name, slug, brand_id, description, status, visibility) VALUES
(13, 1, 'Pixel 6',            'pixel-6-13',    NULL, 'Google Pixel 6 - pure Android experience with excellent camera.', 'active', 'public'),
(14, 1, 'iPhone 13 Mini',     'iphone-13-mini-14', NULL, 'Apple iPhone 13 Mini - compact powerful smartphone.', 'active', 'public'),
(15, 1, 'Redmi Note 11',      'redmi-note-11-15',  NULL, 'Redmi Note 11 - great value mid-range phone.', 'active', 'public'),
(16, 1, 'Galaxy A52s',        'galaxy-a52s-16',    2,    'Samsung Galaxy A52s - balanced specs and camera.', 'active', 'public'),
(17, 1, 'OnePlus 9',          'oneplus-9-17',      NULL, 'OnePlus 9 - flagship performance.', 'active', 'public'),
(18, 1, 'Xperia 5 IV',        'xperia-5-iv-18',    NULL, 'Sony Xperia 5 series - compact and cinematic.', 'active', 'public'),
(19, 1, 'Moto G Power',       'moto-g-power-19',   NULL, 'Motorola Moto G Power - long battery life.', 'active', 'public'),
(20, 1, 'Nokia G20',          'nokia-g20-20',      NULL, 'Nokia G20 - reliable hardware and clean UI.', 'active', 'public'),
(21, 1, 'Oppo Reno',          'oppo-reno-21',      NULL, 'Oppo Reno - stylish design and fast charging.', 'active', 'public'),
(22, 1, 'Vivo Y33',           'vivo-y33-22',       NULL, 'Vivo Y33 - budget smartphone with good cameras.', 'active', 'public');

-- Map Smartphones to category_id = 2
INSERT INTO product_categories (product_id, category_id) VALUES
(13, 2),(14, 2),(15, 2),(16, 2),(17, 2),(18, 2),(19, 2),(20, 2),(21, 2),(22, 2);

-- =============== PRODUCTS: Clothing (category_id = 3) - ids 23..32 ===============
INSERT INTO products (id, store_id, name, slug, brand_id, description, status, visibility) VALUES
(23, 1, 'Casual Shirt',        'casual-shirt-23',      1, 'Lightweight casual shirt for everyday wear.', 'active', 'public'),
(24, 1, 'Denim Jeans',         'denim-jeans-24',       NULL, 'Classic slim-fit denim jeans.', 'active', 'public'),
(25, 1, 'Hoodie Classic',      'hoodie-classic-25',    NULL, 'Comfortable pullover hoodie with kangaroo pocket.', 'active', 'public'),
(26, 1, 'Summer Dress',        'summer-dress-26',      NULL, 'Breathable summer dress in multiple colors.', 'active', 'public'),
(27, 1, 'A-line Skirt',        'a-line-skirt-27',      NULL, 'A-line skirt with comfortable waistband.', 'active', 'public'),
(28, 1, 'Silk Blouse',         'silk-blouse-28',       NULL, 'Elegant silk blouse suitable for office wear.', 'active', 'public'),
(29, 1, 'Sports Jacket',       'sports-jacket-29',     NULL, 'Lightweight sports jacket for outdoor activities.', 'active', 'public'),
(30, 1, 'Socks 5-pack',        'socks-5pack-30',       NULL, 'Multipack cotton socks.', 'active', 'public'),
(31, 1, 'Baseball Cap',        'baseball-cap-31',      NULL, 'Classic baseball cap with adjustable strap.', 'active', 'public'),
(32, 1, 'Summer Shorts',       'summer-shorts-32',     NULL, 'Comfortable summer shorts with pockets.', 'active', 'public');

-- Map Clothing products to category_id = 3
INSERT INTO product_categories (product_id, category_id) VALUES
(23, 3),(24, 3),(25, 3),(26, 3),(27, 3),(28, 3),(29, 3),(30, 3),(31, 3),(32, 3);

-- =============== PRODUCTS: Men (category_id = 4) - ids 33..42 ===============
INSERT INTO products (id, store_id, name, slug, brand_id, description, status, visibility) VALUES
(33, 1, 'Men Polo Shirt',      'men-polo-shirt-33',    NULL, 'Classic polo shirt suitable for casual and semi-formal looks.', 'active', 'public'),
(34, 1, 'Men Slim Jeans',      'men-slim-jeans-34',    NULL, 'Slim-fit jeans tailored for men.', 'active', 'public'),
(35, 1, 'Men Hoodie',          'men-hoodie-35',        NULL, 'Men''s hoodie with soft fleece lining.', 'active', 'public'),
(36, 1, 'Men Formal Shirt',    'men-formal-shirt-36',  NULL, 'Formal shirt ideal for office and events.', 'active', 'public'),
(37, 1, 'Men Chinos',          'men-chinos-37',        NULL, 'Comfortable chino pants in multiple colors.', 'active', 'public'),
(38, 1, 'Men Blazer',          'men-blazer-38',        NULL, 'Tailored blazer for formal occasions.', 'active', 'public'),
(39, 1, 'Men Leather Belt',    'men-leather-belt-39',  NULL, 'Genuine leather belt with classic buckle.', 'active', 'public'),
(40, 1, 'Men Running Shorts',  'men-running-shorts-40',NULL, 'Breathable running shorts with reflective details.', 'active', 'public'),
(41, 1, 'Men Swim Trunks',     'men-swim-trunks-41',   NULL, 'Quick-dry swim trunks with inner mesh.', 'active', 'public'),
(42, 1, 'Men Winter Coat',     'men-winter-coat-42',   NULL, 'Insulated winter coat with water-resistant shell.', 'active', 'public');

-- Map Men products to category_id = 4
INSERT INTO product_categories (product_id, category_id) VALUES
(33, 4),(34, 4),(35, 4),(36, 4),(37, 4),(38, 4),(39, 4),(40, 4),(41, 4),(42, 4);

-- =============== SKUS for all new products (one sku per product) ===============
-- Existing skus used earlier: 1..5. New skus start at id = 6
INSERT INTO skus (id, product_id, sku_code, barcode, price, compare_at_price, cost_price, weight_gram, status) VALUES
(6,  3,  'PRD-3-SKU',  '1000000000006', 1200000.00, NULL, 700000.00, 300, 'active'),
(7,  4,  'PRD-4-SKU',  '1000000000007',  900000.00, NULL, 400000.00, 800, 'active'),
(8,  5,  'PRD-5-SKU',  '1000000000008', 2500000.00, NULL,1500000.00, 50, 'active'),
(9,  6,  'PRD-6-SKU',  '1000000000009',12000000.00, NULL,8000000.00,8000, 'active'),
(10, 7,  'PRD-7-SKU',  '1000000000010',25000000.00, NULL,17000000.00,1400,'active'),
(11, 8,  'PRD-8-SKU',  '1000000000011', 8000000.00, NULL,5000000.00,3500,'active'),
(12, 9,  'PRD-9-SKU',  '1000000000012', 200000.00, NULL,  60000.00,100, 'active'),
(13,10,  'PRD-10-SKU', '1000000000013',2000000.00, NULL,1200000.00,100, 'active'),
(14,11,  'PRD-11-SKU', '1000000000014',1500000.00, NULL, 800000.00,600, 'active'),
(15,12,  'PRD-12-SKU', '1000000000015',4000000.00, NULL,2200000.00,250, 'active'),
(16,13,  'PRD-13-SKU', '1000000000016',14000000.00, NULL,9000000.00,200,'active'),
(17,14,  'PRD-14-SKU', '1000000000017',22000000.00, NULL,14000000.00,160,'active'),
(18,15,  'PRD-15-SKU', '1000000000018', 4000000.00, NULL,2300000.00,180,'active'),
(19,16,  'PRD-16-SKU', '1000000000019', 8500000.00, NULL,5200000.00,180,'active'),
(20,17,  'PRD-17-SKU', '1000000000020',12000000.00, NULL,7000000.00,190,'active'),
(21,18,  'PRD-18-SKU', '1000000000021',11000000.00, NULL,6500000.00,160,'active'),
(22,19,  'PRD-19-SKU', '1000000000022', 3500000.00, NULL,1800000.00,220,'active'),
(23,20,  'PRD-20-SKU', '1000000000023', 3000000.00, NULL,1600000.00,210,'active'),
(24,21,  'PRD-21-SKU', '1000000000024', 7000000.00, NULL,4200000.00,175,'active'),
(25,22,  'PRD-22-SKU', '1000000000025', 4500000.00, NULL,2500000.00,170,'active'),
(26,23,  'PRD-23-SKU', '1000000000026', 300000.00, NULL,120000.00,350,'active'),
(27,24,  'PRD-24-SKU', '1000000000027', 600000.00, NULL,300000.00,700,'active'),
(28,25,  'PRD-25-SKU', '1000000000028', 500000.00, NULL,250000.00,700,'active'),
(29,26,  'PRD-26-SKU', '1000000000029', 800000.00, NULL,400000.00,400,'active'),
(30,27,  'PRD-27-SKU', '1000000000030', 450000.00, NULL,200000.00,250,'active'),
(31,28,  'PRD-28-SKU', '1000000000031', 750000.00, NULL,380000.00,150,'active'),
(32,29,  'PRD-29-SKU', '1000000000032',1200000.00, NULL,600000.00,600,'active'),
(33,30,  'PRD-30-SKU', '1000000000033', 150000.00, NULL, 60000.00,50, 'active'),
(34,31,  'PRD-31-SKU', '1000000000034', 180000.00, NULL, 70000.00,80, 'active'),
(35,32,  'PRD-32-SKU', '1000000000035', 260000.00, NULL,100000.00,120,'active'),
(36,33,  'PRD-33-SKU', '1000000000036', 320000.00, NULL,120000.00,300,'active'),
(37,34,  'PRD-34-SKU', '1000000000037', 650000.00, NULL,350000.00,700,'active'),
(38,35,  'PRD-35-SKU', '1000000000038', 520000.00, NULL,240000.00,700,'active'),
(39,36,  'PRD-36-SKU', '1000000000039', 700000.00, NULL,380000.00,250,'active'),
(40,37,  'PRD-37-SKU', '1000000000040', 680000.00, NULL,360000.00,600,'active'),
(41,38,  'PRD-38-SKU', '1000000000041',1800000.00, NULL,900000.00,1200,'active'),
(42,39,  'PRD-39-SKU', '1000000000042', 220000.00, NULL, 90000.00,150,'active'),
(43,40,  'PRD-40-SKU', '1000000000043', 280000.00, NULL,110000.00,160,'active'),
(44,41,  'PRD-41-SKU', '1000000000044', 360000.00, NULL,150000.00,200,'active'),
(45,42,  'PRD-42-SKU', '1000000000045',2800000.00, NULL,1600000.00,2200,'active');

-- =============== INVENTORIES for new skus ===============
INSERT INTO inventories (sku_id, quantity, reserved) VALUES
(6,  50, 0),(7,  40, 0),(8,  60, 0),(9,  10, 0),(10, 8, 0),
(11, 20, 0),(12,120,0),(13, 30, 0),(14,  6, 0),(15, 25, 0),
(16, 15, 0),(17,  5, 0),(18, 50, 0),(19, 18, 0),(20, 12, 0),
(21, 22, 0),(22, 30, 0),(23, 28, 0),(24, 14, 0),(25, 20, 0),
(26,100, 0),(27, 60, 0),(28, 70, 0),(29, 45, 0),(30, 80, 0),
(31, 40, 0),(32, 35, 0),(33,150,0),(34, 90, 0),(35,110,0),
(36, 55, 0),(37, 40, 0),(38, 60, 0),(39, 70, 0),(40, 85, 0),
(41, 12, 0),(42,140,0),(43, 95, 0),(44, 60, 0),(45, 7,  0);

-- =============== OPTIONAL: product_images (one image per product) ===============
INSERT INTO product_images (product_id, image_url, position) VALUES
(3,  'https://cdn.example.com/products/wireless-headphones.jpg', 1),
(4,  'https://cdn.example.com/products/bluetooth-speaker.jpg',   1),
(5,  'https://cdn.example.com/products/smartwatch-x.jpg',        1),
(6,  'https://cdn.example.com/products/4k-tv-55.jpg',            1),
(7,  'https://cdn.example.com/products/laptop-pro-14.jpg',       1),
(8,  'https://cdn.example.com/products/gaming-console.jpg',      1),
(9,  'https://cdn.example.com/products/usb-c-charger.jpg',       1),
(10, 'https://cdn.example.com/products/external-ssd-1tb.jpg',    1),
(11, 'https://cdn.example.com/products/wifi6-router.jpg',        1),
(12, 'https://cdn.example.com/products/action-camera.jpg',       1),
(13, 'https://cdn.example.com/products/pixel6.jpg',              1),
(14, 'https://cdn.example.com/products/iphone13-mini.jpg',       1),
(15, 'https://cdn.example.com/products/redmi-note11.jpg',        1),
(16, 'https://cdn.example.com/products/galaxy-a52s.jpg',         1),
(17, 'https://cdn.example.com/products/oneplus9.jpg',            1),
(18, 'https://cdn.example.com/products/xperia5iv.jpg',           1),
(19, 'https://cdn.example.com/products/moto-g-power.jpg',        1),
(20, 'https://cdn.example.com/products/nokia-g20.jpg',           1),
(21, 'https://cdn.example.com/products/oppo-reno.jpg',           1),
(22, 'https://cdn.example.com/products/vivo-y33.jpg',            1),
(23, 'https://cdn.example.com/products/casual-shirt.jpg',        1),
(24, 'https://cdn.example.com/products/denim-jeans.jpg',         1),
(25, 'https://cdn.example.com/products/hoodie-classic.jpg',      1),
(26, 'https://cdn.example.com/products/summer-dress.jpg',        1),
(27, 'https://cdn.example.com/products/a-line-skirt.jpg',        1),
(28, 'https://cdn.example.com/products/silk-blouse.jpg',         1),
(29, 'https://cdn.example.com/products/sports-jacket.jpg',       1),
(30, 'https://cdn.example.com/products/socks-5pack.jpg',         1),
(31, 'https://cdn.example.com/products/baseball-cap.jpg',        1),
(32, 'https://cdn.example.com/products/summer-shorts.jpg',       1),
(33, 'https://cdn.example.com/products/men-polo-shirt.jpg',      1),
(34, 'https://cdn.example.com/products/men-slim-jeans.jpg',      1),
(35, 'https://cdn.example.com/products/men-hoodie.jpg',          1),
(36, 'https://cdn.example.com/products/men-formal-shirt.jpg',    1),
(37, 'https://cdn.example.com/products/men-chinos.jpg',          1),
(38, 'https://cdn.example.com/products/men-blazer.jpg',          1),
(39, 'https://cdn.example.com/products/men-leather-belt.jpg',    1),
(40, 'https://cdn.example.com/products/men-running-shorts.jpg',  1),
(41, 'https://cdn.example.com/products/men-swim-trunks.jpg',     1),
(42, 'https://cdn.example.com/products/men-winter-coat.jpg',     1);

-- End - now each of categories 1 (Electronics), 2 (Smartphones), 3 (Clothing), and 4 (Men)
-- has at least 10 products inserted and mapped via product_categories.

-- End of seed data