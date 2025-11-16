# 🎯 HƯỚNG DẪN TEST API ORDER/CREATE BẰNG POSTMAN

## 📋 BƯỚC 1: Import dữ liệu mẫu vào Database

```bash
mysql -u root -p marketplace_db < /Users/letu/Downloads/DoAnJava09/db_scripts/insert_sample_data.sql
```

Hoặc copy và paste nội dung file SQL vào MySQL Workbench/phpMyAdmin.

## 🚀 BƯỚC 2: Restart Server

```bash
cd /Users/letu/Downloads/DoAnJava09/backend
./mvnw spring-boot:run
```

Hoặc click nút **Run** trong IntelliJ IDEA.

## 📬 BƯỚC 3: Test API trong Postman

### ✅ Test 1: Tạo đơn hàng đơn giản (KHÔNG CẦN TOKEN)

**Method:** `POST`  
**URL:** `http://localhost:8080/api/orders/create`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "userId": 1,
  "address": "123 Nguyễn Huệ, Quận 1, TP.HCM",
  "orderItems": [
    {
      "productId": 1,
      "storeId": 1,
      "quantity": 1,
      "price": 25000000
    }
  ]
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "userId": 1,
  "total": 25000000.00,
  "status": "PENDING",
  "createdAt": "2025-10-10T...",
  "address": "123 Nguyễn Huệ, Quận 1, TP.HCM",
  "voucherId": null,
  "orderItems": [
    {
      "id": 1,
      "productId": 1,
      "storeId": 1,
      "quantity": 1,
      "price": 25000000.00
    }
  ]
}
```

---

### ✅ Test 2: Tạo đơn hàng với nhiều sản phẩm

```json
{
  "userId": 1,
  "address": "456 Lê Lợi, Quận 3, TP.HCM",
  "orderItems": [
    {
      "productId": 1,
      "storeId": 1,
      "quantity": 1,
      "price": 25000000
    },
    {
      "productId": 4,
      "storeId": 2,
      "quantity": 3,
      "price": 150000
    }
  ]
}
```

**Expected total:** 25,000,000 + (150,000 × 3) = 25,450,000

---

### ✅ Test 3: Tạo đơn hàng với voucher

```json
{
  "userId": 2,
  "voucherId": 1,
  "address": "789 Trần Hưng Đạo, Quận 5, TP.HCM",
  "orderItems": [
    {
      "productId": 2,
      "storeId": 1,
      "quantity": 1,
      "price": 20000000
    }
  ]
}
```

---

### ✅ Test 4: Xem tất cả đơn hàng

**Method:** `GET`  
**URL:** `http://localhost:8080/api/orders`

---

### ✅ Test 5: Xem đơn hàng theo ID

**Method:** `GET`  
**URL:** `http://localhost:8080/api/orders/1`

---

### ✅ Test 6: Xem đơn hàng của user

**Method:** `GET`  
**URL:** `http://localhost:8080/api/orders/user/1`

---

### ✅ Test 7: Cập nhật trạng thái đơn hàng

**Method:** `PUT`  
**URL:** `http://localhost:8080/api/orders/1/status`

**Body:**
```json
{
  "status": "PAID"
}
```

---

### ✅ Test 8: Hủy đơn hàng

**Method:** `PUT`  
**URL:** `http://localhost:8080/api/orders/1/cancel`

---

## 🎨 POSTMAN COLLECTION (Import vào Postman)

Lưu nội dung dưới đây vào file `OrderAPI.postman_collection.json` và import vào Postman:

```json
{
  "info": {
    "name": "Order Management API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Order - Simple",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"userId\": 1,\n  \"address\": \"123 Nguyễn Huệ, Q1, TP.HCM\",\n  \"orderItems\": [\n    {\n      \"productId\": 1,\n      \"storeId\": 1,\n      \"quantity\": 1,\n      \"price\": 25000000\n    }\n  ]\n}"
        },
        "url": {"raw": "http://localhost:8080/api/orders/create"}
      }
    },
    {
      "name": "Create Order - Multiple Items",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"userId\": 1,\n  \"address\": \"456 Lê Lợi, Q3, TP.HCM\",\n  \"orderItems\": [\n    {\n      \"productId\": 1,\n      \"storeId\": 1,\n      \"quantity\": 1,\n      \"price\": 25000000\n    },\n    {\n      \"productId\": 4,\n      \"storeId\": 2,\n      \"quantity\": 3,\n      \"price\": 150000\n    }\n  ]\n}"
        },
        "url": {"raw": "http://localhost:8080/api/orders/create"}
      }
    },
    {
      "name": "Get All Orders",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/orders"}
      }
    },
    {
      "name": "Get Order By ID",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/orders/1"}
      }
    },
    {
      "name": "Get Orders By User",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/orders/user/1"}
      }
    },
    {
      "name": "Update Order Status",
      "request": {
        "method": "PUT",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"status\": \"PAID\"\n}"
        },
        "url": {"raw": "http://localhost:8080/api/orders/1/status"}
      }
    },
    {
      "name": "Cancel Order",
      "request": {
        "method": "PUT",
        "url": {"raw": "http://localhost:8080/api/orders/1/cancel"}
      }
    }
  ]
}
```

---

## ⚠️ TROUBLESHOOTING

### Lỗi 403 Forbidden
✅ **ĐÃ FIX** - SecurityConfig đã được cập nhật để cho phép `/api/orders/**` không cần token

### Lỗi 404 Not Found
- Kiểm tra URL: `http://localhost:8080/api/orders/create` (có `/api`)
- Restart server

### Lỗi 400 Bad Request
- Kiểm tra JSON format
- Đảm bảo `userId`, `productId`, `storeId` tồn tại trong database

### Lỗi 500 Internal Server Error
- Kiểm tra database đã có dữ liệu mẫu
- Xem console log để biết chi tiết lỗi

---

## 📊 DỮ LIỆU CÓ SẴN TRONG DATABASE

### Users:
- ID 1: Nguyễn Văn A (buyer)
- ID 2: Trần Thị B (buyer)
- ID 3: Lê Văn C (seller)

### Stores:
- ID 1: Shop Điện Tử ABC
- ID 2: Shop Thời Trang XYZ

### Products:
- ID 1: iPhone 15 Pro (25,000,000 VNĐ)
- ID 2: Samsung S24 (20,000,000 VNĐ)
- ID 3: MacBook Pro (35,000,000 VNĐ)
- ID 4: Áo Thun (150,000 VNĐ)
- ID 5: Quần Jean (350,000 VNĐ)

### Vouchers:
- ID 1: GIAM10K (giảm 10,000 VNĐ)
- ID 2: GIAM15 (giảm 15%)

---

## ✅ CHECKLIST

- [x] Fix OrderController mapping thành `/api/orders`
- [x] Fix SecurityConfig cho phép `/api/orders/**` không cần token
- [x] Tạo file SQL với dữ liệu mẫu
- [x] Tạo Postman collection
- [x] Viết hướng dẫn chi tiết

**BÂY GIỜ BẠN CÓ THỂ TEST API! 🎉**

