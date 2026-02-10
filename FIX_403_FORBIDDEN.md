# 🚨 SOLUTION: Fix 403 Forbidden Error

## ❌ Problem
You got **403 Forbidden** when calling:
```
POST http://localhost:8080/api/v1/paddy-stock/add
```

## ✅ Solution: Get Bearer Token First

### 🎯 QUICK FIX (3 Steps)

#### Step 1: Make Sure Application is Running

**Open Terminal and run:**
```bash
cd /Users/dulandinuja/Desktop/D/Rice_Mill_Backend
mvn spring-boot:run
```

**Wait until you see:**
```
Started RiceMillApplication in X.XXX seconds
Tomcat started on port 8080 (http)
```

---

#### Step 2: Get Bearer Token

**Open a NEW terminal window and run:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "admin123"
  }'
```

**You'll get a response like:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNjk2MTYwMCwiZXhwIjoxNzA2OTY1MjAwfQ.abc123...",
    "user": {
      "username": "admin"
    }
  }
}
```

**➡️ COPY the `accessToken` value (the long string after "accessToken":**

---

#### Step 3: Use Token in Your Request

**In Postman:**

1. **Open your "Add Paddy Stock" request**

2. **Go to "Authorization" tab**

3. **Select "Bearer Token" from Type dropdown**

4. **Paste your token in the "Token" field:**
   ```
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNjk2MTYwMCwiZXhwIjoxNzA2OTY1MjAwfQ.abc123...
   ```

5. **Click "Send"**

6. **✅ Success!** You should now get a 200/201 response!

---

## 📱 POSTMAN - Detailed Steps with Screenshots

### Setup 1: Create Login Request

1. Click **"New"** → **"HTTP Request"**
2. Set method to **POST**
3. URL: `http://localhost:8080/api/v1/auth/login`
4. Go to **"Headers"** tab:
   - Add: `Content-Type` = `application/json`
5. Go to **"Body"** tab:
   - Select **"raw"**
   - Select **"JSON"** from dropdown
   - Paste:
   ```json
   {
     "usernameOrEmail": "admin",
     "password": "admin123"
   }
   ```
6. Click **"Send"**
7. **Copy the accessToken from response**

### Setup 2: Use Token in Paddy Stock Request

1. Open your **"Add Paddy Stock"** request
2. Click **"Authorization"** tab
3. **Type:** Select **"Bearer Token"**
4. **Token:** Paste your copied token
5. Keep your Body tab as is:
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
6. Click **"Send"**
7. **✅ You should now get 200 OK response!**

---

## 🤖 AUTOMATED SOLUTION

### Option 1: Use the Script

I created a script that does everything automatically:

```bash
cd /Users/dulandinuja/Desktop/D/Rice_Mill_Backend
./get-bearer-token.sh
```

This will:
1. Check if app is running
2. Login and get token
3. Save token to `.bearer-token.txt`
4. Test the token with a sample API call
5. Show you how to use it

### Option 2: One-Line Command

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin123"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4
```

This will output just the token - copy and use it!

---

## 🔄 Complete Testing Flow

### 1. Start Application
```bash
cd /Users/dulandinuja/Desktop/D/Rice_Mill_Backend
mvn spring-boot:run
```

### 2. Get Token (in new terminal)
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"admin123"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

echo "Your token: $TOKEN"
```

### 3. Test Add Paddy Stock
```bash
curl -X POST http://localhost:8080/api/v1/paddy-stock/add \
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
    "batchNumber": "PADDY-20260203-0001",
    "notes": "Premium Basmati"
  }'
```

### 4. Success Response
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

### Error: "Connection refused"
**Cause:** Application not running
**Fix:**
```bash
cd /Users/dulandinuja/Desktop/D/Rice_Mill_Backend
mvn spring-boot:run
```

### Error: 403 Forbidden
**Cause:** No token or token not in Authorization header
**Fix:** Add `Authorization: Bearer YOUR_TOKEN` header

### Error: 401 Unauthorized
**Cause:** Token expired or invalid
**Fix:** Get a new token by logging in again

### Error: "Invalid username or password"
**Cause:** Wrong credentials
**Fix:** Use `admin` / `admin123`

### Port 8080 in use
**Fix:**
```bash
lsof -ti:8080 | xargs kill -9
```

---

## 📋 Quick Reference Card

```
┌─────────────────────────────────────────────────────────┐
│  DEFAULT CREDENTIALS                                     │
├─────────────────────────────────────────────────────────┤
│  Username: admin                                         │
│  Password: admin123                                      │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  LOGIN ENDPOINT                                          │
├─────────────────────────────────────────────────────────┤
│  POST http://localhost:8080/api/v1/auth/login          │
│                                                          │
│  Body:                                                   │
│  {                                                       │
│    "usernameOrEmail": "admin",                          │
│    "password": "admin123"                               │
│  }                                                       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  AUTHORIZATION HEADER FORMAT                             │
├─────────────────────────────────────────────────────────┤
│  Authorization: Bearer YOUR_TOKEN_HERE                   │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Checklist

Before making API calls:
- [ ] Application is running on port 8080
- [ ] You've logged in and got access token
- [ ] Token is added to Authorization header
- [ ] Authorization type is "Bearer Token"
- [ ] Token starts with "eyJ..."

---

## 🎯 Summary

**Why 403 Forbidden?**
→ All API endpoints (except login/register) require authentication

**How to fix?**
→ Get Bearer token from login endpoint

**How to use token?**
→ Add to Authorization header: `Bearer YOUR_TOKEN`

**In Postman:**
→ Authorization tab → Type: Bearer Token → Paste token

**Token expired?**
→ Login again to get new token

---

## 📚 Related Files

- `HOW_TO_GET_BEARER_TOKEN.md` - Complete token guide
- `get-bearer-token.sh` - Automated token retrieval script
- `POSTMAN_TESTING_GUIDE.md` - Full Postman testing guide
- `POSTMAN_CURL_COMMANDS.txt` - All cURL commands

---

**✅ You're all set! Follow the 3 steps above and you'll be testing successfully!** 🚀

