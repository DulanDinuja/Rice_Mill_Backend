-- Update paddy_stock table to match frontend requirements

-- First, drop the foreign key constraint from rice_stock that references paddy_stock.batch_number
SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP FOREIGN KEY fk_rice_source_paddy',
    'SELECT 1'
  )
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND constraint_name = 'fk_rice_source_paddy'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Drop the index on rice_stock that references source_paddy_batch
SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP INDEX idx_rice_stock_source_paddy',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND index_name = 'idx_rice_stock_source_paddy'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Safe drop index helper: only runs ALTER if index exists
SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP INDEX idx_paddy_stock_batch_number',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND index_name = 'idx_paddy_stock_batch_number'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP INDEX idx_paddy_stock_paddy_type_id',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND index_name = 'idx_paddy_stock_paddy_type_id'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Safe drop columns: only runs ALTER if column exists
SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN supplier_contact',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'supplier_contact'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN purchase_date',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'purchase_date'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN warehouse_location',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'warehouse_location'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN moisture_content',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'moisture_content'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN quality_grade',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'quality_grade'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN batch_number',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'batch_number'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE paddy_stock DROP COLUMN notes',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'notes'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Modify existing columns (safe: these will fail only if column absent; wrap in conditional if needed)
ALTER TABLE paddy_stock
  MODIFY COLUMN quantity DECIMAL(10,2) NOT NULL,
  MODIFY COLUMN price_per_kg DECIMAL(10,2) NULL,
  MODIFY COLUMN supplier VARCHAR(255) NULL,
  MODIFY COLUMN total_value DECIMAL(12,2) NULL;

-- Add new columns using prepared statements (MySQL safe approach)
SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN unit VARCHAR(20)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'unit'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN warehouse VARCHAR(255)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'warehouse'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN moisture_level DECIMAL(5,2)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'moisture_level'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN customer_name VARCHAR(255)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'customer_name'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN customer_id VARCHAR(255)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'customer_id'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN mobile_number VARCHAR(20)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'mobile_number'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE paddy_stock ADD COLUMN status VARCHAR(50) DEFAULT \'In Stock\'',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND column_name = 'status'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Create new indexes only if they don't exist (safe check via information_schema)
SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'CREATE INDEX idx_paddy_stock_warehouse ON paddy_stock(warehouse)',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND index_name = 'idx_paddy_stock_warehouse'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'CREATE INDEX idx_paddy_stock_status ON paddy_stock(status)',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND index_name = 'idx_paddy_stock_status'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'CREATE INDEX idx_paddy_stock_customer_id ON paddy_stock(customer_id)',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE table_schema = DATABASE()
    AND table_name = 'paddy_stock'
    AND index_name = 'idx_paddy_stock_customer_id'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;
