#!/bin/bash

# ============================================================================
# Get Bearer Token - Rice Mill Backend
# ============================================================================

echo "╔══════════════════════════════════════════════════════════════╗"
echo "║          🔐 Getting Bearer Token for Rice Mill API          ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""

# Check if app is running
echo "📡 Checking if application is running..."
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "❌ Application is not running!"
    echo ""
    echo "Please start the application first:"
    echo "  cd /Users/dulandinuja/Desktop/D/Rice_Mill_Backend"
    echo "  mvn spring-boot:run"
    echo ""
    exit 1
fi

echo "✅ Application is running!"
echo ""

# Login to get token
echo "🔑 Logging in as admin..."
echo ""

RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }')

# Check if login was successful
if echo "$RESPONSE" | grep -q "accessToken"; then
    # Extract token
    TOKEN=$(echo $RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

    echo "✅ Login successful!"
    echo ""
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                    YOUR BEARER TOKEN                          ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo ""
    echo "$TOKEN"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    # Save to file
    echo "$TOKEN" > .bearer-token.txt
    echo "💾 Token saved to: .bearer-token.txt"
    echo ""

    # Show how to use
    echo "📋 HOW TO USE IN POSTMAN:"
    echo "  1. Go to Authorization tab"
    echo "  2. Type: Bearer Token"
    echo "  3. Token: Paste the token above"
    echo ""

    echo "📋 HOW TO USE IN cURL:"
    echo '  curl -X POST http://localhost:8080/api/v1/paddy-stock/add \'
    echo '    -H "Content-Type: application/json" \'
    echo "    -H \"Authorization: Bearer $TOKEN\" \\"
    echo '    -d '"'"'{"paddyType":"Basmati","quantity":1000,...}'"'"
    echo ""

    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "✅ All set! Use the token in your API requests."
    echo ""

    # Test the token
    echo "🧪 Testing token with Paddy Stock API..."
    TEST_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/paddy-stock/add \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{
        "paddyType": "Basmati",
        "quantity": 1000,
        "pricePerKg": 50,
        "supplier": "ABC Suppliers",
        "supplierContact": "9876543210",
        "purchaseDate": "2026-02-03",
        "warehouseLocation": "Warehouse A",
        "moistureContent": 14.5,
        "qualityGrade": "A",
        "batchNumber": "PADDY-TEST-'$(date +%s)'",
        "notes": "Test entry from token script"
      }')

    if echo "$TEST_RESPONSE" | grep -q "success"; then
        echo "✅ Token is working! API call successful!"
        echo ""
        echo "Response:"
        echo "$TEST_RESPONSE" | head -20
    else
        echo "⚠️  Token obtained but test failed. Response:"
        echo "$TEST_RESPONSE"
    fi

else
    echo "❌ Login failed!"
    echo ""
    echo "Response:"
    echo "$RESPONSE"
    echo ""
    echo "Please check:"
    echo "  1. Application is running (http://localhost:8080)"
    echo "  2. Database is accessible"
    echo "  3. Default admin user exists"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

