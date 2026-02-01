# Postman Test Collection for Registration API

## Registration Endpoint (Public - No Auth Required)

### Register New User
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/register`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
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

### Login with Registered User
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/login`
- **Headers:**
  - `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "usernameOrEmail": "testuser@example.com",
  "password": "Test@12345"
}
```

### Get Current User Info (Requires Auth)
- **Method:** GET
- **URL:** `http://localhost:8080/api/auth/me`
- **Headers:**
  - `Authorization: Bearer {ACCESS_TOKEN}` (from login response)

## Test Scenarios

### 1. Successful Registration
**Expected:** 200 OK with user info

### 2. Password Mismatch
```json
{
  "fullName": "Test User",
  "idNumber": "123456789V",
  "mobileNumber": "+94771234567",
  "email": "test2@example.com",
  "password": "Password123",
  "confirmPassword": "DifferentPass"
}
```
**Expected:** 400 Bad Request - "Passwords do not match"

### 3. Duplicate Email
Register with same email twice.
**Expected:** 400 Bad Request - "Email already exists"

### 4. Invalid Email Format
```json
{
  "fullName": "Test User",
  "idNumber": "123456789V",
  "mobileNumber": "+94771234567",
  "email": "invalid-email",
  "password": "Test@12345",
  "confirmPassword": "Test@12345"
}
```
**Expected:** 400 Bad Request - "Invalid email format"

### 5. Missing Required Fields
```json
{
  "fullName": "",
  "idNumber": "",
  "mobileNumber": "",
  "email": "",
  "password": "",
  "confirmPassword": ""
}
```
**Expected:** 400 Bad Request with validation errors

### 6. Password Too Short
```json
{
  "fullName": "Test User",
  "idNumber": "123456789V",
  "mobileNumber": "+94771234567",
  "email": "test@example.com",
  "password": "12345",
  "confirmPassword": "12345"
}
```
**Expected:** 400 Bad Request - "Password must be at least 6 characters"

## Important Notes

1. **No Bearer Token Required** for `/api/auth/register` - It's now a public endpoint
2. **Username is auto-generated** from email address
3. **New users get STAFF role** automatically
4. **Email must be unique** in the system
5. **Passwords must match** between password and confirmPassword fields

