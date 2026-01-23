-- Add a text address column to orders table
-- This migration is compatible with MySQL/MariaDB
ALTER TABLE `orders`
  ADD COLUMN `address` TEXT;
