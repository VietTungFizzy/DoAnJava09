#!/bin/bash

# API Test Script for Account Management System
# Make sure the Spring Boot application is running on localhost:80

BASE_URL="http://localhost:80"
EMAIL="test@example.com"
PASSWORD="password123"

echo "=== Account Management API Test ==="
echo

# Test 1: Register a new user
echo "1. Testing User Registration..."
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Test User\",
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\",
    \"phone\": \"0123456789\"
  }")

echo "Register Response: $REGISTER_RESPONSE"
echo

# Test 2: Login
echo "2. Testing User Login..."
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }")

echo "Login Response: $LOGIN_RESPONSE"
echo

# Extract token from login response
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Extracted Token: $TOKEN"
echo

if [ -z "$TOKEN" ]; then
    echo "Error: Could not extract token from login response"
    exit 1
fi

# Test 3: Get User Profile
echo "3. Testing Get User Profile..."
PROFILE_RESPONSE=$(curl -s -X GET $BASE_URL/user/profile \
  -H "Authorization: Bearer $TOKEN")

echo "Profile Response: $PROFILE_RESPONSE"
echo

# Test 4: Update User Profile
echo "4. Testing Update User Profile..."
UPDATE_RESPONSE=$(curl -s -X PUT $BASE_URL/user/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Updated Test User\",
    \"phone\": \"0987654321\"
  }")

echo "Update Response: $UPDATE_RESPONSE"
echo

# Test 5: Change Password
echo "5. Testing Change Password..."
CHANGE_PASSWORD_RESPONSE=$(curl -s -X PUT $BASE_URL/user/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"currentPassword\": \"$PASSWORD\",
    \"newPassword\": \"newpassword123\"
  }")

echo "Change Password Response: $CHANGE_PASSWORD_RESPONSE"
echo

# Test 6: Login with new password
echo "6. Testing Login with New Password..."
NEW_LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"newpassword123\"
  }")

echo "New Login Response: $NEW_LOGIN_RESPONSE"
echo

# Extract new token
NEW_TOKEN=$(echo $NEW_LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "New Token: $NEW_TOKEN"
echo

# Test 7: Request Account Deletion
echo "7. Testing Account Deletion Request..."
DELETE_RESPONSE=$(curl -s -X POST $BASE_URL/user/delete-account \
  -H "Authorization: Bearer $NEW_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"password\": \"newpassword123\",
    \"reason\": \"Testing API functionality\"
  }")

echo "Delete Account Response: $DELETE_RESPONSE"
echo

echo "=== API Test Completed ==="
