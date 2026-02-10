# 🧪 Postman Testing Guide - Rice & Paddy Stock APIs

## 📋 Quick Start

### Step 1: Login to Get Access Token

**Endpoint:** `POST http://localhost:8080/api/v1/auth/login`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "usernameOrEmail": "admin",
    "password": "admin123"
}'
```

**Response:**
```json
{
    "success": true,
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "refreshToken": "...",
        "user": {
            "id": "...",
            "username": "admin",
            "email": "admin@ricemill.com"
        }
    }
}
```

**➡️ Copy the `accessToken` value - you'll need it for all subsequent requests!**

---

## 🌾 PADDY STOCK APIs

### 1. Add Paddy Stock

**Endpoint:** `POST http://localhost:8080/api/v1/paddy-stock/add`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/paddy-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "paddyType": "Basmati",
    "quantity": 1000,
    "pricePerKg": 50,
    "supplier": "ABC Suppliers Pvt Ltd",
    "supplierContact": "9876543210",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse A - Section 1",
    "moistureContent": 14.5,
    "qualityGrade": "A",
    "batchNumber": "PADDY-20260203-0001",
    "notes": "Premium quality Basmati paddy"
}'
```

**Response:**
```json
{
    "success": true,
    "message": "Paddy stock added successfully",
    "data": {
        "id": "uuid-here",
        "paddyType": "Basmati",
        "quantity": 1000,
        "pricePerKg": 50,
        "totalValue": 50000,
        "batchNumber": "PADDY-20260203-0001",
        "active": true,
        "createdAt": "2026-02-03T10:30:00"
    }
}
```

### 2. Get All Paddy Stock (Paginated)

**Endpoint:** `GET http://localhost:8080/api/v1/paddy-stock?page=0&size=10`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/paddy-stock?page=0&size=10&sortBy=createdAt&sortOrder=desc' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

**Response:**
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "id": "uuid-1",
                "paddyType": "Basmati",
                "quantity": 1000,
                "pricePerKg": 50,
                "batchNumber": "PADDY-20260203-0001",
                "warehouseLocation": "Warehouse A - Section 1",
                "active": true
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "size": 10,
        "number": 0
    }
}
```

### 3. Get Paddy Stock by ID

**Endpoint:** `GET http://localhost:8080/api/v1/paddy-stock/{id}`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/paddy-stock/PASTE_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

### 4. Update Paddy Stock

**Endpoint:** `PUT http://localhost:8080/api/v1/paddy-stock/{id}`

**cURL:**
```bash
curl --location --request PUT 'http://localhost:8080/api/v1/paddy-stock/PASTE_ID_HERE' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "paddyType": "Basmati",
    "quantity": 1200,
    "pricePerKg": 52,
    "supplier": "ABC Suppliers Pvt Ltd",
    "supplierContact": "9876543210",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse A - Section 1",
    "moistureContent": 14.0,
    "qualityGrade": "A",
    "notes": "Updated stock - Premium quality"
}'
```

### 5. Delete Paddy Stock

**Endpoint:** `DELETE http://localhost:8080/api/v1/paddy-stock/{id}`

**cURL:**
```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/paddy-stock/PASTE_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

---

## 🍚 RICE STOCK APIs

### 1. Add Rice Stock

**Endpoint:** `POST http://localhost:8080/api/v1/rice-stock/add`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/rice-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "riceType": "White Rice",
    "variety": "Basmati Premium",
    "quantity": 800,
    "packageType": "25KG",
    "numberOfPackages": 32,
    "pricePerKg": 80,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse B - Section 2",
    "qualityGrade": "Premium",
    "batchNumber": "RICE-20260203-0001",
    "sourcePaddyBatch": "PADDY-20260203-0001",
    "notes": "Processed from premium Basmati paddy"
}'
```

**Response:**
```json
{
    "success": true,
    "message": "Rice stock added successfully",
    "data": {
        "id": "uuid-here",
        "riceType": "White Rice",
        "variety": "Basmati Premium",
        "quantity": 800,
        "pricePerKg": 80,
        "totalValue": 64000,
        "batchNumber": "RICE-20260203-0001",
        "packageType": "25KG",
        "numberOfPackages": 32,
        "active": true,
        "createdAt": "2026-02-03T11:00:00"
    }
}
```

### 2. Get All Rice Stock (Paginated)

**Endpoint:** `GET http://localhost:8080/api/v1/rice-stock?page=0&size=10`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/rice-stock?page=0&size=10&sortBy=createdAt&sortOrder=desc' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

**Response:**
```json
{
    "success": true,
    "data": {
        "content": [
            {
                "id": "uuid-1",
                "riceType": "White Rice",
                "variety": "Basmati Premium",
                "quantity": 800,
                "pricePerKg": 80,
                "batchNumber": "RICE-20260203-0001",
                "packageType": "25KG",
                "numberOfPackages": 32,
                "warehouseLocation": "Warehouse B - Section 2",
                "active": true
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "size": 10,
        "number": 0
    }
}
```

### 3. Get Rice Stock by ID

**Endpoint:** `GET http://localhost:8080/api/v1/rice-stock/{id}`

**cURL:**
```bash
curl --location 'http://localhost:8080/api/v1/rice-stock/PASTE_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

### 4. Update Rice Stock

**Endpoint:** `PUT http://localhost:8080/api/v1/rice-stock/{id}`

**cURL:**
```bash
curl --location --request PUT 'http://localhost:8080/api/v1/rice-stock/PASTE_ID_HERE' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "riceType": "White Rice",
    "variety": "Basmati Premium",
    "quantity": 900,
    "packageType": "25KG",
    "numberOfPackages": 36,
    "pricePerKg": 85,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse B - Section 2",
    "qualityGrade": "Premium",
    "notes": "Updated - Premium quality rice"
}'
```

### 5. Delete Rice Stock

**Endpoint:** `DELETE http://localhost:8080/api/v1/rice-stock/{id}`

**cURL:**
```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/rice-stock/PASTE_ID_HERE' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

---

## 📝 MORE TEST EXAMPLES

### Add Multiple Paddy Stock Entries

#### Entry 1: Nadu Paddy
```bash
curl --location 'http://localhost:8080/api/v1/paddy-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "paddyType": "Nadu",
    "quantity": 1500,
    "pricePerKg": 45,
    "supplier": "XYZ Agro Traders",
    "supplierContact": "9123456789",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse A - Section 2",
    "moistureContent": 15.0,
    "qualityGrade": "B",
    "batchNumber": "PADDY-20260203-0002",
    "notes": "Nadu variety paddy"
}'
```

#### Entry 2: Samba Paddy
```bash
curl --location 'http://localhost:8080/api/v1/paddy-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "paddyType": "Samba",
    "quantity": 2000,
    "pricePerKg": 48,
    "supplier": "Golden Harvest Suppliers",
    "supplierContact": "9988776655",
    "purchaseDate": "2026-02-03",
    "warehouseLocation": "Warehouse B - Section 1",
    "moistureContent": 13.5,
    "qualityGrade": "A",
    "batchNumber": "PADDY-20260203-0003",
    "notes": "High quality Samba paddy"
}'
```

### Add Multiple Rice Stock Entries

#### Entry 1: Brown Rice
```bash
curl --location 'http://localhost:8080/api/v1/rice-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "riceType": "Brown Rice",
    "variety": "Nadu",
    "quantity": 1000,
    "packageType": "50KG",
    "numberOfPackages": 20,
    "pricePerKg": 70,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse A - Section 3",
    "qualityGrade": "Standard",
    "batchNumber": "RICE-20260203-0002",
    "sourcePaddyBatch": "PADDY-20260203-0002",
    "notes": "Brown Nadu rice"
}'
```

#### Entry 2: Parboiled Rice
```bash
curl --location 'http://localhost:8080/api/v1/rice-stock/add' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE' \
--data '{
    "riceType": "Parboiled Rice",
    "variety": "Samba",
    "quantity": 1500,
    "packageType": "25KG",
    "numberOfPackages": 60,
    "pricePerKg": 75,
    "processingDate": "2026-02-03",
    "expiryDate": "2027-02-03",
    "warehouseLocation": "Warehouse B - Section 3",
    "qualityGrade": "Premium",
    "batchNumber": "RICE-20260203-0003",
    "sourcePaddyBatch": "PADDY-20260203-0003",
    "notes": "Premium parboiled Samba rice"
}'
```

---

## 🔍 TESTING FILTERS & SEARCH

### Get Paddy Stock with Sorting
```bash
curl --location 'http://localhost:8080/api/v1/paddy-stock?page=0&size=10&sortBy=quantity&sortOrder=desc' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

### Get Rice Stock with Pagination
```bash
curl --location 'http://localhost:8080/api/v1/rice-stock?page=1&size=5' \
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN_HERE'
```

---

## 📦 POSTMAN SETUP INSTRUCTIONS

### Method 1: Import the Postman Collection

1. **Download the collection file**: `Rice_Mill_APIs.postman_collection.json`
2. Open Postman
3. Click **Import** button (top left)
4. Drag and drop the JSON file or click **Upload Files**
5. Click **Import**

### Method 2: Manual Setup in Postman

#### Step 1: Create a New Collection
1. Open Postman
2. Click **New** > **Collection**
3. Name it: "Rice Mill APIs"

#### Step 2: Create Environment Variables
1. Click **Environments** (left sidebar)
2. Click **Create Environment**
3. Name it: "Rice Mill Dev"
4. Add variables:
   - `baseUrl` = `http://localhost:8080`
   - `token` = (leave empty, will be set automatically)

#### Step 3: Add Requests to Collection

**For each API endpoint:**
1. Click **Add Request**
2. Set the request type (GET, POST, PUT, DELETE)
3. Enter the URL: `{{baseUrl}}/api/v1/paddy-stock/add`
4. Add Headers:
   - `Content-Type`: `application/json`
   - `Authorization`: `Bearer {{token}}`
5. Add Request Body (for POST/PUT):
   - Select **raw** and **JSON**
   - Paste the JSON from the cURL examples above

#### Step 4: Auto-Save Token After Login

In the **Login** request, add this to the **Tests** tab:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.accessToken);
    console.log("Token saved:", jsonData.data.accessToken);
}
```

---

## 🎯 TESTING WORKFLOW

### Complete Test Flow:

1. **Login** → Get access token ✅
2. **Add Paddy Stock** → Create 3 entries ✅
3. **Get All Paddy Stock** → Verify entries ✅
4. **Add Rice Stock** → Create 3 entries ✅
5. **Get All Rice Stock** → Verify entries ✅
6. **Update One Entry** → Test update ✅
7. **Get By ID** → Verify updated data ✅
8. **Delete One Entry** → Test deletion ✅
9. **Get All Again** → Verify deletion ✅

---

## 🚨 COMMON ERRORS & SOLUTIONS

### Error: "Port 8080 already in use"
**Solution:**
```bash
lsof -ti:8080 | xargs kill -9
```

### Error: "Unauthorized" (401)
**Solution:** Your token expired. Login again to get a new token.

### Error: "Batch number already exists" (409)
**Solution:** Change the batch number to a unique value.

### Error: "Invalid date format"
**Solution:** Use format `YYYY-MM-DD` (e.g., `2026-02-03`)

---

## 📊 RESPONSE CODES

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input data |
| 401 | Unauthorized | Missing or invalid token |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate entry (e.g., batch number) |
| 500 | Server Error | Internal server error |

---

## 🔗 USEFUL LINKS

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/docs
- **Health Check**: http://localhost:8080/actuator/health

---

**Happy Testing! 🎉**

**Need Help?**
- Check application logs: `tail -f /Users/dulandinuja/Desktop/D/Rice_Mill_Backend/app.log`
- Verify application is running: `curl http://localhost:8080/actuator/health`
- Run test script: `./test-apis.sh`

