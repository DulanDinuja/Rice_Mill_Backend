# Rice Stock API Changes - Frontend Integration

## Summary
Updated the Rice Stock API to match the frontend requirements. Removed unnecessary fields and added customer-related fields.

## Changes Made

### 1. **Entity Changes** (`RiceStock.java`)
**Removed Fields:**
- `variety` - Rice variety
- `packageType` - Package type (1KG, 5KG, etc.)
- `numberOfPackages` - Number of packages
- `processingDate` - Processing date
- `expiryDate` - Expiry date
- `sourcePaddyBatch` - Source paddy batch reference
- `notes` - Additional notes

**Added Fields:**
- `unit` - Unit of measurement (default: "kg")
- `customerName` - Customer name
- `customerId` - Customer ID
- `mobileNumber` - Customer mobile number
- `status` - Stock status (default: "In Stock")

**Retained Fields:**
- `riceType` - Type of rice
- `quantity` - Quantity in stock
- `pricePerKg` - Price per kilogram
- `warehouseLocation` - Warehouse location
- `qualityGrade` - Quality grade (A, B, C)
- `batchNumber` - Unique batch number (auto-generated)
- `totalValue` - Total value (auto-calculated)

### 2. **DTO Changes** (`RiceStockDto.java`)

**AddRequest Fields (Frontend → Backend):**
```java
{
  "riceType": "string",      // Required - Type of rice
  "quantity": number,         // Required - Quantity in kg
  "unit": "string",          // Optional - Unit (default: "kg")
  "warehouse": "string",     // Optional - Warehouse location
  "grade": "string",         // Optional - Quality grade
  "pricePerKg": number,      // Required - Price per kg
  "customerName": "string",  // Optional - Customer name
  "customerId": "string",    // Optional - Customer ID
  "mobileNumber": "string",  // Optional - Mobile number (10-15 digits)
  "status": "string"         // Optional - Status (default: "In Stock")
}
```

**Response Fields (Backend → Frontend):**
```java
{
  "id": "uuid",
  "riceType": "string",
  "quantity": number,
  "unit": "string",
  "warehouseLocation": "string",
  "qualityGrade": "string",
  "pricePerKg": number,
  "customerName": "string",
  "customerId": "string",
  "mobileNumber": "string",
  "status": "string",
  "batchNumber": "string",
  "totalValue": number,
  "createdAt": "datetime",
  "createdBy": "string"
}
```

### 3. **API Endpoints**

**Add Rice Stock:**
```
POST /api/v1/rice-stock/add
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "riceType": "Basmati Rice",
  "quantity": 500,
  "unit": "kg",
  "warehouse": "Main Warehouse",
  "grade": "A",
  "pricePerKg": 75.50,
  "customerName": "John Doe",
  "customerId": "CUST001",
  "mobileNumber": "1234567890",
  "status": "In Stock"
}
```

**Get All Rice Stocks:**
```
GET /api/v1/rice-stock?riceType=Basmati&warehouseLocation=Main&page=0&size=20
```

**Get Rice Stock by ID:**
```
GET /api/v1/rice-stock/{id}
```

**Get Rice Stock by Batch Number:**
```
GET /api/v1/rice-stock/batch/{batchNumber}
```

**Get Rice Stock Summary:**
```
GET /api/v1/rice-stock/summary
```

### 4. **Removed Endpoints**
- `GET /api/v1/rice-stock/date-range` - Date range filtering (processingDate removed)
- `GET /api/v1/rice-stock/source-paddy/{batchNumber}` - Source paddy batch lookup

### 5. **Database Migration** (`V12__update_rice_stock_table.sql`)

**SQL Changes:**
```sql
-- Add new columns
ALTER TABLE rice_stock 
ADD COLUMN customer_name VARCHAR(255),
ADD COLUMN customer_id VARCHAR(100),
ADD COLUMN mobile_number VARCHAR(20),
ADD COLUMN unit VARCHAR(10) DEFAULT 'kg',
ADD COLUMN status VARCHAR(50) DEFAULT 'In Stock';

-- Drop foreign key constraint
ALTER TABLE rice_stock 
DROP FOREIGN KEY IF EXISTS fk_rice_source_paddy;

-- Drop unused columns
ALTER TABLE rice_stock 
DROP COLUMN IF EXISTS variety,
DROP COLUMN IF EXISTS package_type,
DROP COLUMN IF EXISTS number_of_packages,
DROP COLUMN IF EXISTS processing_date,
DROP COLUMN IF EXISTS expiry_date,
DROP COLUMN IF EXISTS source_paddy_batch,
DROP COLUMN IF EXISTS notes;
```

### 6. **Validation Rules**

- `riceType`: Required, max 100 characters
- `quantity`: Required, must be > 0, max 8 digits with 2 decimals
- `pricePerKg`: Required, must be > 0, max 8 digits with 2 decimals
- `mobileNumber`: Optional, must be 10-15 digits if provided
- `unit`: Optional, max 10 characters
- `warehouse`: Optional, max 255 characters
- `grade`: Optional, max 20 characters
- `status`: Optional, max 50 characters

### 7. **Auto-Generated Fields**

- `batchNumber`: Auto-generated unique identifier (format: RICE-YYYYMMDD-XXXXX)
- `totalValue`: Auto-calculated (quantity × pricePerKg)
- `createdAt`, `updatedAt`: Auto-populated timestamps
- `createdBy`, `updatedBy`: Auto-populated from authenticated user

## Frontend Integration Example

```javascript
// Add Rice Stock
const addRiceStock = async (formData) => {
  const response = await fetch('/api/v1/rice-stock/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      riceType: formData.riceType,
      quantity: parseFloat(formData.quantity),
      unit: formData.unit || 'kg',
      warehouse: formData.warehouse,
      grade: formData.grade || 'A',
      pricePerKg: parseFloat(formData.pricePerKg),
      customerName: formData.customerName,
      customerId: formData.customerId,
      mobileNumber: formData.mobileNumber,
      status: formData.status || 'In Stock'
    })
  });
  
  return response.json();
};
```

## Testing

After starting the application, the database migration will automatically apply. Test the API using:

1. **Postman Collection**: Use the existing collection and update the request body
2. **cURL Command**:
```bash
curl -X POST http://localhost:8080/api/v1/rice-stock/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "riceType": "Basmati Rice",
    "quantity": 500,
    "unit": "kg",
    "warehouse": "Main Warehouse",
    "grade": "A",
    "pricePerKg": 75.50,
    "customerName": "John Doe",
    "customerId": "CUST001",
    "mobileNumber": "1234567890",
    "status": "In Stock"
  }'
```

## Migration Path

1. **Backup Database**: Before running, backup your current database
2. **Run Application**: Start the Spring Boot application
3. **Flyway Migration**: V12 migration will automatically execute
4. **Verify**: Check that new columns exist and old columns are removed
5. **Test API**: Test all endpoints with new field structure

## Notes

- Batch numbers are auto-generated and cannot be manually set
- All existing rice stock records will need to have default values for new fields
- The `totalValue` is automatically calculated on save/update
- Customer fields are optional and can be null
- Default values: `unit="kg"`, `status="In Stock"`

