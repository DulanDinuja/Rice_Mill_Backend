#!/bin/bash

# Test Registration API - No Authentication Required

echo "Testing Registration Endpoint..."
echo "================================"
echo ""

# Test 1: Successful Registration
echo "Test 1: Register New User"
echo "--------------------------"
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "idNumber": "123456789V",
    "mobileNumber": "+94771234567",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }' | json_pp

echo ""
echo ""

# Test 2: Login with new user
echo "Test 2: Login with New User"
echo "----------------------------"
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser@example.com",
    "password": "Test@12345"
  }' | json_pp

echo ""
echo ""

# Test 3: Password mismatch
echo "Test 3: Password Mismatch Error"
echo "--------------------------------"
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Another User",
    "idNumber": "987654321V",
    "mobileNumber": "+94779876543",
    "email": "another@example.com",
    "password": "Test@12345",
    "confirmPassword": "DifferentPassword"
  }' | json_pp

echo ""
echo ""

# Test 4: Duplicate email
echo "Test 4: Duplicate Email Error"
echo "------------------------------"
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Duplicate User",
    "idNumber": "111222333V",
    "mobileNumber": "+94771112222",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }' | json_pp

echo ""
echo ""
echo "Tests Complete!"

