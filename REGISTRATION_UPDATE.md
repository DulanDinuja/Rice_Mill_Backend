# Registration API Update - Summary

## Changes Made

I've successfully updated your registration endpoint to match your frontend requirements and made it publicly accessible (no authentication required).

### 1. Updated Registration Request DTO (`AuthDto.java`)
**New Fields:**
- `fullName` - User's full name
- `idNumber` - User's ID number
- `mobileNumber` - User's mobile/phone number  
- `email` - User's email address
- `password` - User's password (min 6 characters)
- `confirmPassword` - Password confirmation

**Removed Fields:**
- `username` (now auto-generated from email)
- `roles` (now defaults to STAFF for public registration)

### 2. Updated AuthController (`AuthController.java`)
- Removed `@PreAuthorize("hasRole('ADMIN')")` annotation
- Registration endpoint is now publicly accessible

### 3. Updated SecurityConfig (`SecurityConfig.java`)
- Added `/api/auth/register` to permitAll() endpoints
- No authentication required for registration

### 4. Updated User Entity (`User.java`)
- Added `idNumber` field (VARCHAR 50)
- Added `mobileNumber` field (VARCHAR 20)

### 5. Updated AuthService (`AuthService.java`)
- Added password confirmation validation
- Auto-generates username from email
- Sets default role to STAFF for all new registrations
- Saves idNumber and mobileNumber fields

### 6. Created Database Migration (`V2__add_user_id_and_mobile.sql`)
- Adds new columns to users table

## API Usage

### Register New User (Public Endpoint - No Auth Required)

**Endpoint:** `POST http://localhost:8080/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
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

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "uuid-here",
    "username": "testuser@example.com",
    "email": "testuser@example.com",
    "fullName": "Test User",
    "roles": ["STAFF"]
  },
  "timestamp": "2026-02-01T11:00:00"
}
```

**Error Responses:**

- **Passwords don't match (400):**
```json
{
  "success": false,
  "message": "Passwords do not match",
  "timestamp": "2026-02-01T11:00:00"
}
```

- **Email already exists (400):**
```json
{
  "success": false,
  "message": "Email already exists",
  "timestamp": "2026-02-01T11:00:00"
}
```

- **Validation errors (400):**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "fullName": "Full name is required",
    "email": "Invalid email format",
    "password": "Password must be at least 6 characters"
  },
  "timestamp": "2026-02-01T11:00:00"
}
```

## Testing Steps

1. **Restart your application** to apply the changes
2. **The database migration will run automatically** when the app starts (Flyway)
3. **Test registration without authentication:**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "idNumber": "987654321V",
    "mobileNumber": "+94771234567",
    "email": "john.doe@example.com",
    "password": "SecurePass123",
    "confirmPassword": "SecurePass123"
  }'
```

4. **Test login with the new user:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john.doe@example.com",
    "password": "SecurePass123"
  }'
```

## Key Changes Summary

✅ Registration is now **public** (no authentication required)  
✅ Frontend fields match: Name, ID Number, Mobile Number, Email, Password, Confirm Password  
✅ Password confirmation validation added  
✅ Username is auto-generated from email  
✅ New users automatically get STAFF role  
✅ Database migration added for new fields  
✅ No more 403 "Invalid or malformed JWT" errors on registration

## Files Modified

1. `/src/main/java/com/ricemill/dto/AuthDto.java`
2. `/src/main/java/com/ricemill/controller/AuthController.java`
3. `/src/main/java/com/ricemill/config/SecurityConfig.java`
4. `/src/main/java/com/ricemill/entity/User.java`
5. `/src/main/java/com/ricemill/service/AuthService.java`

## Files Created

1. `/src/main/resources/db/migration/V2__add_user_id_and_mobile.sql`

