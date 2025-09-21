# Voucher/Coupon Management API Documentation

## Overview
This API provides comprehensive voucher and coupon management functionality for the e-commerce platform.

## Base URL
```
http://localhost:8080/vouchers
```

## Endpoints

### 1. List Vouchers
**GET** `/vouchers`

Query Parameters:
- `keyword` (optional): Search by voucher code or ID
- `status` (optional): Filter by status (ACTIVE, INACTIVE, EXPIRED)
- `storeId` (optional): Filter by store ID
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "code": "SAVE10",
      "type": "PERCENT",
      "value": 10.00,
      "maxDiscount": 50000.00,
      "minOrderTotal": 100000.00,
      "storeId": null,
      "startDate": "2024-01-01T00:00:00",
      "endDate": "2024-12-31T23:59:59",
      "usageLimit": 1000,
      "usageLimitPerUser": 1,
      "status": "ACTIVE",
      "createdAt": "2024-01-01T00:00:00",
      "isExpired": false,
      "isActive": true,
      "description": "Giảm 10% cho đơn hàng từ 100000 VND (tối đa 50000 VND)"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### 2. Get Voucher by ID
**GET** `/vouchers/{id}`

**Response:**
```json
{
  "id": 1,
  "code": "SAVE10",
  "type": "PERCENT",
  "value": 10.00,
  "maxDiscount": 50000.00,
  "minOrderTotal": 100000.00,
  "storeId": null,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-12-31T23:59:59",
  "usageLimit": 1000,
  "usageLimitPerUser": 1,
  "status": "ACTIVE",
  "createdAt": "2024-01-01T00:00:00",
  "isExpired": false,
  "isActive": true,
  "description": "Giảm 10% cho đơn hàng từ 100000 VND (tối đa 50000 VND)"
}
```

### 3. Get Voucher by Code
**GET** `/vouchers/code/{code}`

**Response:** Same as above

### 4. Create Voucher
**POST** `/vouchers`

**Request Body:**
```json
{
  "code": "NEWYEAR20",
  "type": "PERCENT",
  "value": 20.00,
  "maxDiscount": 100000.00,
  "minOrderTotal": 200000.00,
  "storeId": 1,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-01-31T23:59:59",
  "usageLimit": 500,
  "usageLimitPerUser": 2,
  "status": "ACTIVE"
}
```

**Response:** VoucherResponse object

### 5. Update Voucher
**PUT** `/vouchers/{id}`

**Request Body:** Same as create

**Response:** Updated VoucherResponse object

### 6. Delete Voucher
**DELETE** `/vouchers/{id}`

**Response:** 204 No Content

### 7. Get Active Vouchers
**GET** `/vouchers/active`

**Response:** Array of active VoucherResponse objects

### 8. Get Active Vouchers by Store
**GET** `/vouchers/active/store/{storeId}`

**Response:** Array of active VoucherResponse objects for the store

### 9. Get Applicable Vouchers
**GET** `/vouchers/applicable?storeId={storeId}&orderTotal={orderTotal}`

Query Parameters:
- `storeId` (optional): Store ID
- `orderTotal` (optional): Order total amount

**Response:** Array of applicable VoucherResponse objects

### 10. Calculate Discount
**POST** `/vouchers/calculate-discount?voucherCode={code}&orderTotal={amount}&storeId={storeId}`

Query Parameters:
- `voucherCode`: Voucher code to apply
- `orderTotal`: Order total amount
- `storeId` (optional): Store ID

**Response:**
```json
25000.00
```

## Voucher Types

### FIXED
- Provides a fixed amount discount
- Example: 50,000 VND off

### PERCENT
- Provides a percentage discount
- Example: 10% off (value = 10)

## Voucher Status

- `ACTIVE`: Voucher is active and can be used
- `INACTIVE`: Voucher is disabled
- `EXPIRED`: Voucher has expired

## Business Rules

1. **Code Uniqueness**: Voucher codes must be unique across the system
2. **Date Validation**: Start date cannot be after end date
3. **Value Validation**: 
   - Fixed vouchers: Value must be > 0
   - Percent vouchers: Value must be between 0-100
4. **Usage Limits**: Both total usage and per-user usage limits are enforced
5. **Store Restrictions**: Vouchers can be limited to specific stores
6. **Minimum Order**: Vouchers can require a minimum order total
7. **Maximum Discount**: Percent vouchers can have a maximum discount cap

## Error Responses

### 400 Bad Request
```json
{
  "error": "Voucher code already exists: SAVE10"
}
```

### 404 Not Found
```json
{
  "error": "Voucher not found with id: 999"
}
```

### 422 Unprocessable Entity
```json
{
  "error": "Voucher value must be greater than 0"
}
```

## Example Usage Scenarios

### 1. Create a Store-Specific Voucher
```bash
curl -X POST http://localhost:8080/vouchers \
  -H "Content-Type: application/json" \
  -d '{
    "code": "STORE10",
    "type": "PERCENT",
    "value": 10.00,
    "maxDiscount": 50000.00,
    "minOrderTotal": 100000.00,
    "storeId": 1,
    "startDate": "2024-01-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "usageLimit": 100,
    "usageLimitPerUser": 1,
    "status": "ACTIVE"
  }'
```

### 2. Calculate Discount for an Order
```bash
curl -X POST "http://localhost:8080/vouchers/calculate-discount?voucherCode=SAVE10&orderTotal=200000&storeId=1"
```

### 3. Get Applicable Vouchers for a Customer
```bash
curl "http://localhost:8080/vouchers/applicable?storeId=1&orderTotal=150000"
```

## Database Schema

The voucher system uses the following main table:

```sql
CREATE TABLE vouchers (
  id                   INT AUTO_INCREMENT PRIMARY KEY,
  code                 VARCHAR(50) UNIQUE,
  type                 ENUM('fixed','percent') NOT NULL,
  value                DECIMAL(12,2) NOT NULL,
  max_discount         DECIMAL(12,2) NULL,
  min_order_total      DECIMAL(12,2) NULL,
  store_id             INT NULL,
  start_date           DATETIME,
  end_date             DATETIME,
  usage_limit          INT NULL,
  usage_limit_per_user INT NULL,
  status               ENUM('active','inactive','expired') DEFAULT 'active',
  created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (store_id) REFERENCES stores(id)
);
```
