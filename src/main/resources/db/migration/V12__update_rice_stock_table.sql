-- Add new columns for customer information using prepared statements for safety
SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE rice_stock ADD COLUMN customer_name VARCHAR(255)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'customer_name'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE rice_stock ADD COLUMN customer_id VARCHAR(100)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'customer_id'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE rice_stock ADD COLUMN mobile_number VARCHAR(20)',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'mobile_number'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE rice_stock ADD COLUMN unit VARCHAR(10) DEFAULT \'kg\'',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'unit'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)=0,
    'ALTER TABLE rice_stock ADD COLUMN status VARCHAR(50) DEFAULT \'In Stock\'',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'status'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Drop foreign key constraint if it exists (safe approach)
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

-- Drop check constraint before dropping the processing_date column
SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP CHECK chk_rice_expiry_date',
    'SELECT 1'
  )
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND constraint_name = 'chk_rice_expiry_date'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Drop unused columns using prepared statements for safety
SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN variety',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'variety'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN package_type',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'package_type'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN number_of_packages',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'number_of_packages'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN processing_date',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'processing_date'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN expiry_date',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'expiry_date'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN source_paddy_batch',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'source_paddy_batch'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @s = (
  SELECT IF(
    COUNT(*)>0,
    'ALTER TABLE rice_stock DROP COLUMN notes',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE table_schema = DATABASE()
    AND table_name = 'rice_stock'
    AND column_name = 'notes'
);
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

