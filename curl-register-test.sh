  #!/bin/bash

echo "=========================================="
echo "Registration API - cURL Test"
echo "=========================================="
echo ""

# Check if server is running
echo "Checking if server is running..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Server is running"
    echo ""
else
    echo "❌ Server is not running on port 8080"
    echo "Please start the server first"
    exit 1
fi

# Test 1: Register New User
echo "Test 1: Register New User"
echo "--------------------------"
echo "Request:"
echo "POST http://localhost:8080/api/auth/register"
echo ""

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "idNumber": "123456789V",
    "mobileNumber": "+94771234567",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | python3 -m json.tool 2>/dev/null || cat

echo ""
echo ""

# Test 2: Login with registered user
echo "Test 2: Login with Registered User"
echo "-----------------------------------"
echo "Request:"
echo "POST http://localhost:8080/api/auth/login"
echo ""

RESPONSE=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser@example.com",
    "password": "Test@12345"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s)

echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"

# Extract access token
ACCESS_TOKEN=$(echo "$RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['accessToken'])" 2>/dev/null)

echo ""
echo ""

# Test 3: Get current user info with token
if [ ! -z "$ACCESS_TOKEN" ]; then
    echo "Test 3: Get Current User Info (with token)"
    echo "------------------------------------------"
    echo "Request:"
    echo "GET http://localhost:8080/api/auth/me"
    echo "Authorization: Bearer [TOKEN]"
    echo ""

    curl -X GET http://localhost:8080/api/auth/me \
      -H "Authorization: Bearer $ACCESS_TOKEN" \
      -w "\n\nHTTP Status: %{http_code}\n" \
      -s | python3 -m json.tool 2>/dev/null || cat

    echo ""
    echo ""
fi

# Test 4: Password Mismatch
echo "Test 4: Password Mismatch (Should Fail)"
echo "----------------------------------------"
echo "Request:"
echo "POST http://localhost:8080/api/auth/register"
echo ""

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Another User",
    "idNumber": "987654321V",
    "mobileNumber": "+94779876543",
    "email": "another@example.com",
    "password": "Test@12345",
    "confirmPassword": "DifferentPassword"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | python3 -m json.tool 2>/dev/null || cat

echo ""
echo ""

# Test 5: Duplicate Email
echo "Test 5: Duplicate Email (Should Fail)"
echo "--------------------------------------"
echo "Request:"
echo "POST http://localhost:8080/api/auth/register"
echo ""

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Duplicate User",
    "idNumber": "111222333V",
    "mobileNumber": "+94771112222",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | python3 -m json.tool 2>/dev/null || cat

echo ""
echo ""

# Test 6: Invalid Email Format
echo "Test 6: Invalid Email Format (Should Fail)"
echo "-------------------------------------------"
echo "Request:"
echo "POST http://localhost:8080/api/auth/register"
echo ""

curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "idNumber": "123456789V",
    "mobileNumber": "+94771234567",
    "email": "invalid-email",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }' \
  -w "\n\nHTTP Status: %{http_code}\n" \
  -s | python3 -m json.tool 2>/dev/null || cat

echo ""
echo ""

echo "=========================================="
echo "Tests Complete!"
echo "=========================================="

