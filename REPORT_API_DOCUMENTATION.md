# Report API Documentation - Ameera Rice Inventory System

## Overview
This document provides comprehensive documentation for the Reports API endpoints in the Ameera Rice Mill Inventory System.

## Base URL
```
http://localhost:8080/api/reports
```

## Authentication
All endpoints require JWT Bearer token authentication.

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. Get Reports Data
Fetch filtered report data based on type and criteria.

**Endpoint:** `GET /api/reports`

**Authentication:** Required (ADMIN, MANAGER roles)

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| reportType | String | Yes | Type of report | `paddy_threshing`, `paddy_sale`, `paddy_add_stock`, `rice_sale`, `rice_add_stock` |
| fromDate | DateTime | No | Start date (ISO 8601) | `2025-01-01T00:00:00` |
| toDate | DateTime | No | End date (ISO 8601) | `2025-02-02T23:59:59` |
| warehouse | String | No | Warehouse name filter | `Main Warehouse` |
| paddyType | String | No | Paddy type filter | `Nadu`, `Keeri Samba`, `Samba` |
| riceType | String | No | Rice type filter | `White Raw`, `Steam Nadu`, `Steam Keeri`, `Red Raw`, `Keeri White Raw` |
| supplier | String | No | Supplier name filter | `Farmer Co-op A` |
| page | Integer | No | Page number (default: 1) | `1` |
| limit | Integer | No | Records per page (default: 100, max: 1000) | `100` |

**Valid Report Types:**
- `paddy_threshing` - Paddy threshing operations
- `paddy_sale` - Paddy sales transactions
- `paddy_add_stock` - Paddy stock additions
- `rice_sale` - Rice sales transactions
- `rice_add_stock` - Rice stock additions

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports?reportType=paddy_sale&fromDate=2025-01-01T00:00:00&toDate=2025-02-02T23:59:59&warehouse=Main%20Warehouse&page=1&limit=100" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "paddyType": "Nadu",
        "riceType": null,
        "quantity": 1000.00,
        "unit": "KG",
        "moistureLevel": 14.00,
        "warehouse": "Main Warehouse",
        "supplier": "Farmer Co-op A",
        "pricePerKg": null,
        "actionType": "OUTBOUND",
        "date": "2025-01-15T08:00:00",
        "createdAt": "2025-01-15T08:00:00",
        "updatedAt": "2025-01-15T08:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "totalPages": 5,
      "totalRecords": 50,
      "limit": 100,
      "hasNextPage": true,
      "hasPreviousPage": false
    },
    "summary": {
      "totalRecords": 50,
      "totalQuantity": 12500.00,
      "dateRange": {
        "from": "2025-01-01T00:00:00",
        "to": "2025-02-02T23:59:59"
      }
    }
  },
  "message": null,
  "timestamp": "2025-02-02T10:30:00"
}
```

**Error Responses:**

```json
// 400 Bad Request - Invalid report type
{
  "success": false,
  "error": "Invalid report type: invalid_type",
  "code": "INVALID_REPORT_TYPE",
  "timestamp": "2025-02-02T10:30:00"
}

// 400 Bad Request - Invalid date range
{
  "success": false,
  "error": "From date must be earlier than to date",
  "code": "INVALID_DATE_RANGE",
  "timestamp": "2025-02-02T10:30:00"
}

// 401 Unauthorized
{
  "success": false,
  "error": "Authentication required",
  "code": "UNAUTHORIZED",
  "timestamp": "2025-02-02T10:30:00"
}
```

---

### 2. Get Chart Data
Get aggregated data for chart visualization.

**Endpoint:** `GET /api/reports/chart`

**Authentication:** Required (ADMIN, MANAGER roles)

**Query Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| reportType | String | Yes | Type of report | `paddy_sale` |
| fromDate | DateTime | No | Start date (ISO 8601) | `2025-01-01T00:00:00` |
| toDate | DateTime | No | End date (ISO 8601) | `2025-02-02T23:59:59` |
| warehouse | String | No | Warehouse name filter | `Main Warehouse` |
| paddyType | String | No | Paddy type filter | `Nadu` |
| riceType | String | No | Rice type filter | `White Raw` |
| supplier | String | No | Supplier name filter | `Farmer Co-op A` |
| groupBy | String | No | Grouping period (default: month) | `day`, `week`, `month` |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports/chart?reportType=paddy_sale&fromDate=2025-01-01T00:00:00&toDate=2025-02-02T23:59:59&groupBy=month" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "data": [
      {
        "period": "2025-01",
        "label": "Jan",
        "rice": 4000.00,
        "paddy": 2400.00,
        "quantity": 6400.00,
        "recordCount": 15
      },
      {
        "period": "2025-02",
        "label": "Feb",
        "rice": 3000.00,
        "paddy": 1800.00,
        "quantity": 4800.00,
        "recordCount": 12
      }
    ],
    "summary": {
      "totalQuantity": 11200.00,
      "averagePerPeriod": 5600.00,
      "periods": 2
    }
  },
  "message": null,
  "timestamp": "2025-02-02T10:30:00"
}
```

---

### 3. Export Report
Export report data to CSV or PDF format.

**Endpoint:** `POST /api/reports/export`

**Authentication:** Required (ADMIN, MANAGER roles)

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "reportType": "paddy_sale",
  "format": "csv",
  "fromDate": "2025-01-01T00:00:00",
  "toDate": "2025-02-02T23:59:59",
  "filters": {
    "warehouse": "Main Warehouse",
    "paddyType": "Nadu",
    "riceType": null,
    "supplier": "Farmer Co-op A"
  },
  "options": {
    "includeChart": true,
    "includeSummary": true,
    "fileName": "Custom_Report_Name.csv"
  }
}
```

**Example Request:**
```bash
curl -X POST "http://localhost:8080/api/reports/export" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "paddy_sale",
    "format": "csv",
    "fromDate": "2025-01-01T00:00:00",
    "toDate": "2025-02-02T23:59:59",
    "filters": {
      "warehouse": "Main Warehouse",
      "paddyType": "Nadu"
    }
  }' \
  --output report.csv
```

**Success Response (200 OK):**
- Content-Type: `text/csv` or `application/pdf`
- Content-Disposition: `attachment; filename="Paddy_Sale_Report_2025-02-02.csv"`
- Body: File content as stream

**CSV Format Example:**
```csv
Paddy Sale Report
Generated: 2025-02-02 10:30:00
Date Range: 2025-01-01 to 2025-02-02
Total Records: 50

Paddy Type,Quantity,Unit,Moisture %,Warehouse,Supplier,Action Type,Date
Nadu,1000.00,KG,14.00,Main Warehouse,Farmer Co-op A,OUTBOUND,2025-01-15T08:00:00
Keeri Samba,750.00,KG,12.00,Warehouse B,Farmer Co-op B,OUTBOUND,2025-01-20T10:30:00
```

**Note:** PDF export is not yet implemented. Use CSV format.

---

### 4. Get Warehouses List
Get list of all active warehouses.

**Endpoint:** `GET /api/reports/warehouses`

**Authentication:** Required (ADMIN, MANAGER, STAFF roles)

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports/warehouses" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Main Warehouse",
      "location": "Mumbai",
      "capacity": 10000.00,
      "currentStock": 7500.00,
      "active": true
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Warehouse B",
      "location": "Delhi",
      "capacity": 8000.00,
      "currentStock": 4300.00,
      "active": true
    }
  ],
  "message": null,
  "timestamp": "2025-02-02T10:30:00"
}
```

---

### 5. Get Suppliers List
Get list of all active suppliers.

**Endpoint:** `GET /api/reports/suppliers`

**Authentication:** Required (ADMIN, MANAGER, STAFF roles)

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports/suppliers" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Farmer Co-op A",
      "contactNumber": "+91 1234567890",
      "email": "contact@farmercoop-a.com",
      "address": "Village XYZ",
      "active": true
    }
  ],
  "message": null,
  "timestamp": "2025-02-02T10:30:00"
}
```

---

### 6. Get Report Types
Get available report types with metadata.

**Endpoint:** `GET /api/reports/types`

**Authentication:** Required (ADMIN, MANAGER roles)

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports/types" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "value": "paddy_threshing",
      "label": "Paddy Threshing Report",
      "category": "paddy",
      "description": "All threshing operations with moisture levels",
      "columns": ["paddyType", "quantity", "moistureLevel", "warehouse", "supplier", "actionType", "date"]
    },
    {
      "value": "paddy_sale",
      "label": "Paddy Sale Report",
      "category": "paddy",
      "description": "Paddy sales transactions",
      "columns": ["paddyType", "quantity", "moistureLevel", "warehouse", "supplier", "pricePerKg", "actionType", "date"]
    },
    {
      "value": "paddy_add_stock",
      "label": "Paddy Add Stock Report",
      "category": "paddy",
      "description": "Paddy stock additions",
      "columns": ["paddyType", "quantity", "moistureLevel", "warehouse", "supplier", "actionType", "date"]
    },
    {
      "value": "rice_sale",
      "label": "Rice Sale Report",
      "category": "rice",
      "description": "Rice sales transactions",
      "columns": ["riceType", "quantity", "warehouse", "pricePerKg", "actionType", "date"]
    },
    {
      "value": "rice_add_stock",
      "label": "Rice Add Stock Report",
      "category": "rice",
      "description": "Rice stock additions",
      "columns": ["riceType", "quantity", "warehouse", "actionType", "date"]
    }
  ],
  "message": null,
  "timestamp": "2025-02-02T10:30:00"
}
```

---

### 7. Get Movement Report (Legacy)
Get stock movement report with filters.

**Endpoint:** `GET /api/reports/movements`

**Authentication:** Required (ADMIN, MANAGER roles)

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| type | String | No | Product type (PADDY, RICE) |
| movementType | String | No | Movement type (INBOUND, OUTBOUND, TRANSFER, ADJUSTMENT, PROCESSING) |
| warehouseId | UUID | No | Warehouse ID filter |
| from | DateTime | No | Start date (ISO 8601) |
| to | DateTime | No | End date (ISO 8601) |
| page | Integer | No | Page number (default: 0) |
| size | Integer | No | Page size (default: 20) |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports/movements?type=PADDY&movementType=OUTBOUND&from=2025-01-01T00:00:00&to=2025-02-02T23:59:59&page=0&size=20" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 8. Get Processing Report (Legacy)
Get processing records report.

**Endpoint:** `GET /api/reports/processing`

**Authentication:** Required (ADMIN, MANAGER roles)

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| from | DateTime | No | Start date (ISO 8601) |
| to | DateTime | No | End date (ISO 8601) |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/reports/processing?from=2025-01-01T00:00:00&to=2025-02-02T23:59:59" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Common Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "error": "Invalid input parameter",
  "code": "BAD_REQUEST",
  "timestamp": "2025-02-02T10:30:00"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "error": "Authentication required",
  "code": "UNAUTHORIZED",
  "timestamp": "2025-02-02T10:30:00"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "error": "Insufficient permissions",
  "code": "FORBIDDEN",
  "timestamp": "2025-02-02T10:30:00"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "error": "Internal server error",
  "code": "SERVER_ERROR",
  "timestamp": "2025-02-02T10:30:00"
}
```

---

## Testing with Postman

### Setup
1. Import the API collection (if provided)
2. Set up environment variables:
   - `BASE_URL`: `http://localhost:8080`
   - `TOKEN`: Your JWT authentication token

### Authentication Flow
1. First, login to get JWT token:
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

2. Copy the `accessToken` from the response
3. Use it in all subsequent requests as `Bearer <token>`

---

## Rate Limiting

- Standard endpoints: 100 requests per 15 minutes per user
- Export endpoints: 10 requests per hour per user

---

## Best Practices

1. **Pagination**: Always use pagination for large datasets
2. **Date Ranges**: Specify date ranges to limit result sets
3. **Caching**: Results are cached for 5 minutes for common queries
4. **Export Limits**: Export is limited to 1000 records per request
5. **Error Handling**: Always check the `success` field in the response

---

## Support

For issues or questions, contact: support@ameeraricemill.com

---

## Changelog

### Version 1.0.0 (2025-02-02)
- Initial release
- All 8 endpoints implemented
- CSV export functionality
- Comprehensive filtering and pagination
- Chart data aggregation

### Future Enhancements
- PDF export functionality
- Advanced analytics
- Real-time data updates
- Bulk export options
