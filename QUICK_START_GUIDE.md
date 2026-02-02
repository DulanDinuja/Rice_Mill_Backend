# 🚀 Quick Start Guide - Ameera Rice Mill Reports API

## Prerequisites
- ✅ Java 21 installed
- ✅ Maven 3.6+ installed
- ✅ MySQL 8.0+ running
- ✅ Postman (optional, for testing)

## Step 1: Database Setup

### Create Database
```sql
CREATE DATABASE rice_mill;
USE rice_mill;
```

### Configure Database Connection
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rice_mill
    username: your_username
    password: your_password
```

## Step 2: Build and Run

### Clean and Build
```bash
cd D:\Personal\projects\RICE_MILL\Rice_Mill_Backend
mvn clean install -DskipTests
```

### Run Application
```bash
mvn spring-boot:run
```

Or use the provided batch file:
```bash
.\start.bat
```

### Verify Server is Running
```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

## Step 3: Authentication

### Login (Get JWT Token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Save the `accessToken` from the response!**

Example response:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "...",
    "user": {
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

## Step 4: Test Report Endpoints

### 1. Get Report Types
```bash
curl -X GET http://localhost:8080/api/reports/types \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 2. Get Warehouses List
```bash
curl -X GET http://localhost:8080/api/reports/warehouses \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Get Suppliers List
```bash
curl -X GET http://localhost:8080/api/reports/suppliers \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 4. Get Report Data
```bash
curl -X GET "http://localhost:8080/api/reports?reportType=paddy_sale&page=1&limit=100" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. Get Chart Data
```bash
curl -X GET "http://localhost:8080/api/reports/chart?reportType=rice_sale&groupBy=month" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 6. Export Report (CSV)
```bash
curl -X POST http://localhost:8080/api/reports/export \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "paddy_sale",
    "format": "csv",
    "fromDate": "2025-01-01T00:00:00",
    "toDate": "2025-02-02T23:59:59"
  }' \
  --output report.csv
```

## Step 5: Using Postman

### Import Collection
1. Open Postman
2. Click **Import**
3. Select `Ameera_Rice_Mill_Reports_API.postman_collection.json`
4. Collection will be imported with all endpoints

### Set Environment Variables
1. Create new environment in Postman
2. Add variables:
   - `BASE_URL`: `http://localhost:8080`
   - `TOKEN`: (will be auto-filled after login)

### Test Flow
1. Run **Authentication > Login** request
2. Token will be automatically saved
3. Test any report endpoint

## Step 6: Access Swagger UI

Open browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

Features:
- Interactive API documentation
- Try endpoints directly from browser
- See request/response schemas
- Authentication support

## Common Query Parameters

### Report Types
- `paddy_threshing`
- `paddy_sale`
- `paddy_add_stock`
- `rice_sale`
- `rice_add_stock`

### Date Filters
- Format: ISO 8601 DateTime
- Example: `2025-01-01T00:00:00`

### Pagination
- `page`: Page number (default: 1)
- `limit`: Records per page (default: 100, max: 1000)

### Grouping (for charts)
- `day`: Daily aggregation
- `week`: Weekly aggregation
- `month`: Monthly aggregation (default)

## Troubleshooting

### Issue: Connection refused
**Solution:** Ensure MySQL is running and credentials are correct

### Issue: 401 Unauthorized
**Solution:** Check if JWT token is valid and included in Authorization header

### Issue: 403 Forbidden
**Solution:** User role doesn't have permission for the endpoint

### Issue: Empty data returned
**Solution:** Database might not have test data. Add some stock movements first.

### Issue: Compilation errors
**Solution:** 
```bash
mvn clean compile -DskipTests
```

## Sample Test Data

### Create Warehouse
```bash
curl -X POST http://localhost:8080/api/warehouses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Warehouse",
    "location": "Mumbai",
    "capacity": 10000
  }'
```

### Create Supplier
```bash
curl -X POST http://localhost:8080/api/suppliers \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Farmer Co-op A",
    "phone": "+91 1234567890",
    "address": "Village XYZ"
  }'
```

## Next Steps

1. ✅ Review API Documentation: `REPORT_API_DOCUMENTATION.md`
2. ✅ Check Implementation Details: `REPORTS_IMPLEMENTATION_SUMMARY.md`
3. ✅ Test with Postman Collection
4. ✅ Explore Swagger UI
5. ✅ Add test data via other endpoints
6. ✅ Integrate with frontend

## Useful Commands

### Check Application Logs
```bash
tail -f logs/application.log
```

### Stop Application
```bash
Ctrl + C
```

### Rebuild After Code Changes
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### Run with Different Port
```bash
mvn spring-boot:run -Dserver.port=8081
```

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports/types` | Get report types |
| GET | `/api/reports` | Get report data |
| GET | `/api/reports/chart` | Get chart data |
| POST | `/api/reports/export` | Export report |
| GET | `/api/reports/warehouses` | List warehouses |
| GET | `/api/reports/suppliers` | List suppliers |
| GET | `/api/reports/movements` | Movement report |
| GET | `/api/reports/processing` | Processing report |

## Support

For detailed documentation, see:
- `REPORT_API_DOCUMENTATION.md` - Complete API reference
- `REPORTS_IMPLEMENTATION_SUMMARY.md` - Implementation details
- Swagger UI - Interactive documentation

---

**Happy Coding! 🎉**
