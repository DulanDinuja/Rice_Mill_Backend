#!/bin/bash

# ============================================================================
# Rice Mill Backend - Quick Test cURL Commands
# Copy and paste these commands directly into your terminal
# ============================================================================

echo "🌾 Rice Mill Backend API - Quick Test Commands"
echo "=============================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================================================
# STEP 1: LOGIN AND GET TOKEN
# ============================================================================

echo -e "${BLUE}Step 1: Login to get access token${NC}"
echo "Run this command first:"
echo ""
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "usernameOrEmail": "admin",
    "password": "admin123"
}'
EOF
echo ""
echo -e "${YELLOW}➡️  Copy the accessToken from response and replace YOUR_ACCESS_TOKEN in commands below${NC}"
echo ""
echo "Press Enter to continue..."
read

# ============================================================================
# PADDY STOCK APIs
# ============================================================================

echo ""
echo -e "${GREEN}==================== PADDY STOCK APIs ====================${NC}"
echo ""

# Add Paddy Stock
echo -e "${BLUE}1. Add Paddy Stock (Basmati)${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/paddy-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
--data '{
    "paddyType": "Basmati",
    "quantity": 1000,
    "pricePerKg": 50,
    "supplier": "ABC Suppliers",
    "supplierContact": "9876543210",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse A",
    "moistureContent": 14.5,
    "qualityGrade": "A",
    "batchNumber": "PADDY-20260203-0001",
    "notes": "Premium Basmati"
}'
EOF
echo ""
echo "---"

# Get All Paddy Stock
echo ""
echo -e "${BLUE}2. Get All Paddy Stock${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/paddy-stock?page=0&size=10' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
EOF
echo ""
echo "---"

# Get Paddy Stock by ID
echo ""
echo -e "${BLUE}3. Get Paddy Stock by ID${NC}"
echo "Replace STOCK_ID_HERE with actual ID from previous response:"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/paddy-stock/STOCK_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
EOF
echo ""
echo "---"

# Update Paddy Stock
echo ""
echo -e "${BLUE}4. Update Paddy Stock${NC}"
echo "Replace STOCK_ID_HERE with actual ID:"
cat << 'EOF'
curl --location --request PUT 'http://localhost:8080/api/v1/paddy-stock/STOCK_ID_HERE' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
--data '{
    "paddyType": "Basmati",
    "quantity": 1200,
    "pricePerKg": 52,
    "supplier": "ABC Suppliers",
    "supplierContact": "9876543210",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse A",
    "moistureContent": 14.0,
    "qualityGrade": "A",
    "notes": "Updated stock"
}'
EOF
echo ""
echo "---"

# Delete Paddy Stock
echo ""
echo -e "${BLUE}5. Delete Paddy Stock${NC}"
echo "Replace STOCK_ID_HERE with actual ID:"
cat << 'EOF'
curl --location --request DELETE 'http://localhost:8080/api/v1/paddy-stock/STOCK_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
EOF
echo ""
echo ""

# ============================================================================
# RICE STOCK APIs
# ============================================================================

echo -e "${GREEN}==================== RICE STOCK APIs ====================${NC}"
echo ""

# Add Rice Stock
echo -e "${BLUE}1. Add Rice Stock (White Rice)${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/rice-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
--data '{
    "riceType": "White Rice",
    "variety": "Basmati Premium",
    "quantity": 800,
    "packageType": "25KG",
    "numberOfPackages": 32,
    "pricePerKg": 80,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse B",
    "qualityGrade": "Premium",
    "batchNumber": "RICE-20260203-0001",
    "sourcePaddyBatch": "PADDY-20260203-0001",
    "notes": "Premium white rice"
}'
EOF
echo ""
echo "---"

# Get All Rice Stock
echo ""
echo -e "${BLUE}2. Get All Rice Stock${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/rice-stock?page=0&size=10' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
EOF
echo ""
echo "---"

# Get Rice Stock by ID
echo ""
echo -e "${BLUE}3. Get Rice Stock by ID${NC}"
echo "Replace STOCK_ID_HERE with actual ID from previous response:"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/rice-stock/STOCK_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
EOF
echo ""
echo "---"

# Update Rice Stock
echo ""
echo -e "${BLUE}4. Update Rice Stock${NC}"
echo "Replace STOCK_ID_HERE with actual ID:"
cat << 'EOF'
curl --location --request PUT 'http://localhost:8080/api/v1/rice-stock/STOCK_ID_HERE' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
--data '{
    "riceType": "White Rice",
    "variety": "Basmati Premium",
    "quantity": 900,
    "packageType": "25KG",
    "numberOfPackages": 36,
    "pricePerKg": 85,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse B",
    "qualityGrade": "Premium",
    "notes": "Updated rice stock"
}'
EOF
echo ""
echo "---"

# Delete Rice Stock
echo ""
echo -e "${BLUE}5. Delete Rice Stock${NC}"
echo "Replace STOCK_ID_HERE with actual ID:"
cat << 'EOF'
curl --location --request DELETE 'http://localhost:8080/api/v1/rice-stock/STOCK_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
EOF
echo ""
echo ""

# ============================================================================
# MORE EXAMPLES
# ============================================================================

echo -e "${GREEN}==================== MORE TEST DATA ====================${NC}"
echo ""

echo -e "${BLUE}Add Nadu Paddy:${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/paddy-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
--data '{
    "paddyType": "Nadu",
    "quantity": 1500,
    "pricePerKg": 45,
    "supplier": "XYZ Traders",
    "supplierContact": "9123456789",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse A",
    "moistureContent": 15.0,
    "qualityGrade": "B",
    "batchNumber": "PADDY-20260203-0002",
    "notes": "Nadu variety"
}'
EOF
echo ""
echo ""

echo -e "${BLUE}Add Brown Rice:${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/api/v1/rice-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
--data '{
    "riceType": "Brown Rice",
    "variety": "Nadu",
    "quantity": 1000,
    "packageType": "50KG",
    "numberOfPackages": 20,
    "pricePerKg": 70,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse B",
    "qualityGrade": "Standard",
    "batchNumber": "RICE-20260203-0002",
    "sourcePaddyBatch": "PADDY-20260203-0002",
    "notes": "Brown Nadu rice"
}'
EOF
echo ""
echo ""

# ============================================================================
# HEALTH CHECK
# ============================================================================

echo -e "${GREEN}==================== HEALTH CHECK ====================${NC}"
echo ""
echo -e "${BLUE}Check if application is running:${NC}"
cat << 'EOF'
curl --location 'http://localhost:8080/actuator/health'
EOF
echo ""
echo ""

# ============================================================================
# SWAGGER UI
# ============================================================================

echo -e "${GREEN}==================== DOCUMENTATION ====================${NC}"
echo ""
echo "Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo "API Docs: http://localhost:8080/api/docs"
echo ""

echo -e "${GREEN}✅ All commands ready! Copy and paste to test.${NC}"
echo ""
echo "📖 For detailed guide, see: POSTMAN_TESTING_GUIDE.md"

