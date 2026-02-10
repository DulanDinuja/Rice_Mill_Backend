#!/bin/bash

# Rice Stock API Test Script
# This script tests the updated Rice Stock API endpoints

BASE_URL="http://localhost:8080/api/v1"
TOKEN=""

echo "================================================"
echo "Rice Stock API Test Script"
echo "================================================"
echo ""

# Check if token is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <bearer-token>"
    echo ""
    echo "To get a bearer token, run:"
    echo "  ./get-bearer-token.sh"
    echo ""
    exit 1
fi

TOKEN=$1

echo "1. Testing Add Rice Stock..."
echo "----------------------------"
RESPONSE=$(curl -s -X POST "${BASE_URL}/rice-stock/add" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "riceType": "Basmati Rice",
    "quantity": 500,
    "unit": "kg",
    "warehouse": "Main Warehouse",
    "grade": "A",
    "pricePerKg": 75.50,
    "customerName": "John Doe",
    "customerId": "CUST001",
    "mobileNumber": "1234567890",
    "status": "In Stock"
  }')

echo "$RESPONSE" | jq '.'
BATCH_NUMBER=$(echo "$RESPONSE" | jq -r '.data.batchNumber')
STOCK_ID=$(echo "$RESPONSE" | jq -r '.data.id')
echo ""

if [ "$BATCH_NUMBER" != "null" ]; then
    echo "✓ Rice stock added successfully!"
    echo "  Batch Number: $BATCH_NUMBER"
    echo "  Stock ID: $STOCK_ID"
else
    echo "✗ Failed to add rice stock"
    exit 1
fi

echo ""
echo "2. Testing Get Rice Stock by ID..."
echo "-----------------------------------"
curl -s -X GET "${BASE_URL}/rice-stock/${STOCK_ID}" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.'
echo ""

echo "3. Testing Get Rice Stock by Batch Number..."
echo "----------------------------------------------"
curl -s -X GET "${BASE_URL}/rice-stock/batch/${BATCH_NUMBER}" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.'
echo ""

echo "4. Testing Get All Rice Stocks..."
echo "----------------------------------"
curl -s -X GET "${BASE_URL}/rice-stock?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.data.content | length'
echo ""

echo "5. Testing Get Rice Stock Summary..."
echo "-------------------------------------"
curl -s -X GET "${BASE_URL}/rice-stock/summary" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.'
echo ""

echo "6. Testing Filter by Rice Type..."
echo "----------------------------------"
curl -s -X GET "${BASE_URL}/rice-stock?riceType=Basmati" \
  -H "Authorization: Bearer ${TOKEN}" | jq '.data.content | length'
echo ""

echo "================================================"
echo "All tests completed!"
echo "================================================"

