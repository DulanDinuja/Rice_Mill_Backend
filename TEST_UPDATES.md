# Integration Test Updates - Summary

## Changes Made to AuthRegisterIntegrationTest.java

I've updated the integration tests to match the new registration API structure and functionality.

### What Changed:

#### 1. **Removed Old Fields from RegisterRequest**
- ❌ `setUsername()` - No longer exists (auto-generated from email)
- ❌ `setRoles()` - No longer exists (defaults to STAFF)

#### 2. **Added New Required Fields**
- ✅ `setFullName()` - User's full name
- ✅ `setIdNumber()` - User's ID number
- ✅ `setMobileNumber()` - User's mobile/phone number
- ✅ `setEmail()` - User's email address
- ✅ `setPassword()` - User's password
- ✅ `setConfirmPassword()` - Password confirmation

#### 3. **Updated Test Methods**

##### Test 1: `registerShouldSucceedWithoutAuthentication()` ✨ NEW
Tests that registration works without any authentication (public endpoint).

**Verifies:**
- No Bearer token needed
- User is created successfully
- Username is auto-generated from email
- User gets STAFF role by default
- New fields (idNumber, mobileNumber) are saved

##### Test 2: `registerShouldSucceedForAnyAuthenticatedUser()`
Previously: `registerShouldBeForbiddenForNonAdmin()`

**Changed:**
- Now expects `200 OK` instead of `403 FORBIDDEN`
- Updated to use new RegisterRequest fields
- Reflects that registration is now public (even authenticated users can register)

##### Test 3: `registerShouldSucceedForAdmin()`
**Updated:**
- Uses new RegisterRequest fields
- Verifies with email instead of username
- Confirms username is auto-generated from email

##### Test 4: `registerShouldFailWhenPasswordsDoNotMatch()` ✨ NEW
Tests password confirmation validation.

**Verifies:**
- Returns `400 BAD_REQUEST` when passwords don't match
- Error message: "Passwords do not match"

##### Test 5: `registerShouldFailWhenEmailAlreadyExists()` ✨ NEW
Tests duplicate email validation.

**Verifies:**
- First registration succeeds
- Second registration with same email fails with `400 BAD_REQUEST`
- Error message: "Email already exists"

### Test Structure Summary

```java
// OLD - No longer works
AuthDto.RegisterRequest req = new AuthDto.RegisterRequest();
req.setUsername("testuser");          // ❌ Removed
req.setEmail("test@example.com");
req.setPassword("Test@12345");
req.setRoles(Set.of(UserRole.STAFF)); // ❌ Removed
req.setFullName("Test User");

// NEW - Current structure
AuthDto.RegisterRequest req = new AuthDto.RegisterRequest();
req.setFullName("Test User");           // ✅ Required
req.setIdNumber("123456789V");          // ✅ New field
req.setMobileNumber("+94771234567");    // ✅ New field
req.setEmail("test@example.com");       // ✅ Required
req.setPassword("Test@12345");          // ✅ Required
req.setConfirmPassword("Test@12345");   // ✅ New field
```

### Running the Tests

```bash
# Run all auth integration tests
mvn test -Dtest=AuthRegisterIntegrationTest

# Run specific test
mvn test -Dtest=AuthRegisterIntegrationTest#registerShouldSucceedWithoutAuthentication
```

**Note:** Tests require Docker to be running (uses Testcontainers with MySQL).

### All Tests Now Pass ✅

1. ✅ Public registration without authentication
2. ✅ Registration by authenticated staff user
3. ✅ Registration by admin
4. ✅ Password mismatch validation
5. ✅ Duplicate email validation

### Key Assertions Updated

**Old:**
```java
assertThat(userRepository.findByUsername("testuser").orElseThrow().getActive()).isTrue();
```

**New:**
```java
assertThat(userRepository.findByEmail("testuser@example.com").orElseThrow().getActive()).isTrue();
assertThat(user.getUsername()).isEqualTo("testuser@example.com"); // username = email
assertThat(user.getIdNumber()).isEqualTo("123456789V");
assertThat(user.getMobileNumber()).isEqualTo("+94771234567");
```

## Files Modified

1. `/src/test/java/com/ricemill/AuthRegisterIntegrationTest.java`

## No Compilation Errors ✅

All method resolution errors have been fixed:
- ❌ `Cannot resolve method 'setUsername' in 'RegisterRequest'` - FIXED
- ❌ `Cannot resolve method 'setRoles' in 'RegisterRequest'` - FIXED

The tests now use the correct RegisterRequest structure with the new fields.

