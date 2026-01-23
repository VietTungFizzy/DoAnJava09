-- Add sku_id column to order_items with default 0 and index
ALTER TABLE `order_items`
  ADD COLUMN `sku_id` INT NOT NULL DEFAULT 0,
  ADD INDEX `idx_order_items_sku_id` (`sku_id`);
