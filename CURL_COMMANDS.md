# Quick cURL Commands for Registration API

## 1. Register New User (Copy & Paste)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "idNumber": "123456789V",
    "mobileNumber": "+94771234567",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }'
```

## 2. Login with Registered User

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser@example.com",
    "password": "Test@12345"
  }'
```

## 3. One-liner Registration (No Line Breaks)

```bash
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"fullName":"Test User","idNumber":"123456789V","mobileNumber":"+94771234567","email":"testuser@example.com","password":"Test@12345","confirmPassword":"Test@12345"}'
```

## 4. One-liner Login (No Line Breaks)

```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"usernameOrEmail":"testuser@example.com","password":"Test@12345"}'
```

## 5. Register with Pretty JSON Output

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "idNumber": "123456789V",
    "mobileNumber": "+94771234567",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }' | python3 -m json.tool
```

## 6. Register and Show HTTP Status

```bash
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
  -w "\nHTTP Status: %{http_code}\n" \
  -s | python3 -m json.tool
```

## 7. Save Response to File

```bash
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
  -o registration-response.json
```

## 8. Verbose Mode (See Full Request/Response)

```bash
curl -v -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "idNumber": "123456789V",
    "mobileNumber": "+94771234567",
    "email": "testuser@example.com",
    "password": "Test@12345",
    "confirmPassword": "Test@12345"
  }'
```

---

## For Postman Users

**Method:** `POST`  
**URL:** `http://localhost:8080/api/auth/register`  
**Headers:**
- `Content-Type: application/json`

**Body (raw JSON):**
```json
{
  "fullName": "Test User",
  "idNumber": "123456789V",
  "mobileNumber": "+94771234567",
  "email": "testuser@example.com",
  "password": "Test@12345",
  "confirmPassword": "Test@12345"
}
```

**NO Authorization header needed!** ✅

