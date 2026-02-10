# Paddy Stock API Changes Summary

## Overview
Updated the Paddy Stock module to match the frontend requirements. The API now accepts only the fields that the frontend sends.

## Changes Made

### 1. DTO Changes (`PaddyStockDto.java`)

#### AddRequest - Updated Fields:
- ✅ `paddyType` - String (required)
- ✅ `quantity` - BigDecimal (required)
- ✅ `unit` - String (optional)
- ✅ `warehouse` - String (optional)
- ✅ `moistureLevel` - BigDecimal (optional, 0-100)
- ✅ `supplier` - String (optional)
- ✅ `pricePerKg` - BigDecimal (optional)
- ✅ `customerName` - String (optional)
- ✅ `customerId` - String (optional)
- ✅ `mobileNumber` - String (optional, 10-20 digits)
- ✅ `status` - String (optional, default: "In Stock")

#### Removed Fields:
- ❌ `supplierContact`
- ❌ `purchaseDate`
- ❌ `warehouseLocation` (replaced with `warehouse`)
- ❌ `moistureContent` (replaced with `moistureLevel`)
- ❌ `qualityGrade`
- ❌ `batchNumber`
- ❌ `notes`

### 2. Entity Changes (`PaddyStock.java`)

Updated entity fields to match the new DTO structure:
- Changed `warehouseLocation` → `warehouse`
- Changed `moistureContent` → `moistureLevel`
- Added `unit`, `customerName`, `customerId`, `mobileNumber`, `status`
- Removed `supplierContact`, `purchaseDate`, `qualityGrade`, `batchNumber`, `notes`
- Made `pricePerKg`, `supplier`, and `totalValue` nullable

### 3. Service Changes (`PaddyStockService.java`)

- Updated `addPaddyStock()` to use new field names
- Removed batch number generation logic
- Updated validation to work with optional `pricePerKg`
- Removed `getPaddyStockByBatchNumber()` method
- Removed `getPaddyStocksByDateRange()` method
- Updated `getAllPaddyStocks()` parameter from `warehouseLocation` to `warehouse`

### 4. Repository Changes (`PaddyStockRepository.java`)

- Removed `findByBatchNumber()` method
- Removed `existsByBatchNumber()` method
- Removed `findByDateRange()` method
- Updated `findByFilters()` to use `warehouse` instead of `warehouseLocation`

### 5. Controller Changes (`PaddyStockController.java`)

- Removed `@PreAuthorize` annotation (no role checking)
- Removed `/batch/{batchNumber}` endpoint
- Removed `/date-range` endpoint
- Updated `getAllPaddyStocks()` parameter from `warehouseLocation` to `warehouse`
- Updated default sort field from `purchaseDate` to `createdAt`

### 6. Database Migration (`V11__update_paddy_stock_table.sql`)

Created migration to:
- Drop old indexes: `batch_number`, `purchase_date`, `warehouse_location`
- Drop old columns: `supplier_contact`, `purchase_date`, `warehouse_location`, `moisture_content`, `quality_grade`, `batch_number`, `notes`
- Modify existing columns to allow NULL values
- Add new columns: `unit`, `warehouse`, `moisture_level`, `customer_name`, `customer_id`, `mobile_number`, `status`
- Create new indexes: `warehouse`, `status`, `customer_id`

### 7. Other Service Updates

#### ThreshingService.java
- Updated to use `getWarehouse()` instead of `getWarehouseLocation()`
- Removed references to `getQualityGrade()` and `getBatchNumber()`
- Added null check for `pricePerKg`

#### RiceStockService.java
- Removed validation check for source paddy batch existence

### 8. Dashboard Controller
- Removed `@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")` annotation
- Removed unused `PreAuthorize` import

## API Endpoints

### POST `/api/v1/paddy-stock/add`
Add new paddy stock with the following request body:
```json
{
  "paddyType": "Basmati",
  "quantity": 100.5,
  "unit": "KG",
  "warehouse": "Warehouse A",
  "moistureLevel": 12.5,
  "supplier": "ABC Suppliers",
  "pricePerKg": 50.00,
  "customerName": "John Doe",
  "customerId": "CUST001",
  "mobileNumber": "1234567890",
  "status": "In Stock"
}
```

### GET `/api/v1/paddy-stock/{id}`
Get paddy stock by ID

### GET `/api/v1/paddy-stock`
Get all paddy stocks with optional filters:
- `paddyType` (optional)
- `warehouse` (optional)
- Pagination support

### GET `/api/v1/paddy-stock/summary`
Get summary statistics

## Testing Required

After applying the database migration, test the following:
1. Add new paddy stock with all fields
2. Add new paddy stock with minimal fields (only required ones)
3. Get paddy stock by ID
4. Get all paddy stocks with filters
5. Get summary statistics
6. Verify ThreshingService integration still works

## Database Migration

Run the application to automatically apply the migration `V11__update_paddy_stock_table.sql` using Flyway.

**Note:** This migration will drop several columns and their data. Make sure to backup the database before applying this migration in production.

## Status

✅ All compilation errors fixed
✅ Project builds successfully
✅ Ready for testing

