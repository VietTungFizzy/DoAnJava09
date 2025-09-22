#!/bin/bash

# Simple Login API Test Script
BASE_URL="http://localhost:8080"

echo "=== Testing Login API ==="
echo

# Test với user admin (nếu đã setup database)
echo "1. Testing Login with Admin User..."
curl -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }' \
  -w "\nStatus Code: %{http_code}\n" \
  -s | jq '.' 2>/dev/null || cat

echo -e "\n========================\n"

# Test với user mới đăng ký
echo "2. Testing Login with New User..."
curl -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nguyenvana@example.com",
    "password": "123456"
  }' \
  -w "\nStatus Code: %{http_code}\n" \
  -s | jq '.' 2>/dev/null || cat

echo -e "\n========================\n"

# Test với thông tin sai
echo "3. Testing Login with Wrong Credentials..."
curl -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "wrong@example.com",
    "password": "wrongpassword"
  }' \
  -w "\nStatus Code: %{http_code}\n" \
  -s | jq '.' 2>/dev/null || cat

echo -e "\n========================\n"
echo "Login API Test Complete!"
