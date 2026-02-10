# 🔐 HOW TO GET BEARER TOKEN - Step by Step

## ✅ Quick Answer

### Step 1: Login to Get Token

**Using cURL (Terminal):**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3...",
    "refreshToken": "...",
    "user": {
      "username": "admin",
      "email": "admin@ricemill.com"
    }
  }
}
```

**➡️ Copy the `accessToken` value!**

---

### Step 2: Use the Token in Your Request

**Using cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/paddy-stock/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE" \
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
    "batchNumber": "PADDY-20260203-0001",
    "notes": "Premium Basmati"
  }'
```

**Using Postman:**
1. Open Postman
2. Create new request
3. Go to **Authorization** tab
4. Select **Bearer Token** from dropdown
5. Paste your token in the **Token** field
6. Send your request

---

## 📱 POSTMAN - Complete Setup

### Method 1: Auto-Save Token (Recommended)

**Step 1: Create Login Request**
1. Create a new POST request
2. URL: `http://localhost:8080/api/v1/auth/login`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

**Step 2: Add Auto-Save Script**
Go to **Tests** tab and add:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.accessToken);
    pm.environment.set("refreshToken", jsonData.data.refreshToken);
    console.log("✅ Token saved:", jsonData.data.accessToken.substring(0, 20) + "...");
}
```

**Step 3: Create Environment**
1. Click **Environments** (left sidebar)
2. Click **+** to create new environment
3. Name it: "Rice Mill Dev"
4. Add variable: `token` (leave value empty)
5. Add variable: `baseUrl` = `http://localhost:8080`
6. Save and select this environment

**Step 4: Use Token in Other Requests**
1. Create your API request (e.g., Add Paddy Stock)
2. Go to **Authorization** tab
3. Type: **Bearer Token**
4. Token: `{{token}}`
5. Done! The token will be used automatically

---

## 🔄 Complete Testing Flow

### 1️⃣ Login Request (Postman)

**URL:** `POST http://localhost:8080/api/v1/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "usernameOrEmail": "admin",
  "password": "admin123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "...",
    "user": {
      "id": "uuid-here",
      "username": "admin",
      "email": "admin@ricemill.com",
      "fullName": "Administrator",
      "roles": ["ADMIN"]
    }
  }
}
```

---

### 2️⃣ Add Paddy Stock Request (Postman)

**URL:** `POST http://localhost:8080/api/v1/paddy-stock/add`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {{token}}
```

**Body:**
```json
{
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
}
```

**Expected Response:**
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
    "createdAt": "2026-02-03T..."
  }
}
```

---

## 🐛 Troubleshooting

### Error: 403 Forbidden
**Cause:** No token or invalid token
**Solution:** Login again and get a fresh token

### Error: 401 Unauthorized
**Cause:** Token expired (tokens expire after some time)
**Solution:** Login again to get a new token

### Error: "Connection refused"
**Cause:** Application not running
**Solution:**
```bash
cd /Users/dulandinuja/Desktop/D/Rice_Mill_Backend
mvn spring-boot:run
```

### Error: "Invalid username or password"
**Cause:** Wrong credentials
**Solution:** Use default credentials:
- Username: `admin`
- Password: `admin123`

---

## 📋 Quick Reference

### Default Credentials
```
Username: admin
Password: admin123
```

### Base URL
```
http://localhost:8080
```

### Login Endpoint
```
POST /api/v1/auth/login
```

### Authorization Header Format
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🎯 Testing Script

Save this as `get-token.sh` and run it:

```bash
#!/bin/bash

# Get Bearer Token
echo "🔐 Getting Bearer Token..."

RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }')

# Extract token
TOKEN=$(echo $RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token"
    echo "Response: $RESPONSE"
    exit 1
fi

echo "✅ Token obtained!"
echo ""
echo "Your Bearer Token:"
echo "$TOKEN"
echo ""
echo "Copy this token and use it in Postman:"
echo "Authorization: Bearer $TOKEN"
echo ""

# Save to file
echo "$TOKEN" > .token.txt
echo "💾 Token saved to .token.txt"
```

**Make it executable and run:**
```bash
chmod +x get-token.sh
./get-token.sh
```

---

## 🔄 Token Refresh

Tokens expire. When they do, you can:

**Option 1: Login Again**
- Run the login request to get a new token

**Option 2: Use Refresh Token**
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
  }'
```

---

## 📱 Mobile/Web App Integration

For frontend apps, store the token securely:

```javascript
// After login success
const { accessToken, refreshToken } = response.data;

// Store in localStorage (or secure storage)
localStorage.setItem('access_token', accessToken);
localStorage.setItem('refresh_token', refreshToken);

// Use in API calls
const token = localStorage.getItem('access_token');
const headers = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
};

// Make API call
fetch('http://localhost:8080/api/v1/paddy-stock/add', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify(data)
});
```

---

## ✅ Summary

1. **Login** → Get `accessToken`
2. **Copy token** → Use in Authorization header
3. **Format:** `Authorization: Bearer YOUR_TOKEN`
4. **All requests** need this header (except login/register)

---

**Need Help?**
- Check app is running: `curl http://localhost:8080/actuator/health`
- View Swagger UI: http://localhost:8080/api/swagger-ui.html
- Read guide: `POSTMAN_TESTING_GUIDE.md`

**Quick Test:**
```bash
# 1. Get token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin123"}'

# 2. Copy the accessToken
# 3. Use it in your request
```

---

✅ **You're all set! Get your token and start testing!** 🚀

