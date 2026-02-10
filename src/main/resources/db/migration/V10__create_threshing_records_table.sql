-- Create threshing_records table
CREATE TABLE threshing_records (
    id BINARY(16) PRIMARY KEY,
    batch_number VARCHAR(50) UNIQUE NOT NULL,
    paddy_stock_id BINARY(16) NOT NULL,
    paddy_variety VARCHAR(100) NOT NULL,
    input_paddy_quantity DECIMAL(10,2) NOT NULL,
    output_rice_quantity DECIMAL(10,2) NOT NULL,
    wastage_quantity DECIMAL(10,2) NOT NULL,
    threshing_efficiency DECIMAL(5,2) NOT NULL,
    threshing_date DATE NOT NULL,
    mill_operator_name VARCHAR(100) NOT NULL,
    machine_id VARCHAR(50) NOT NULL,
    machine_type VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    rice_stock_id BINARY(16),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP NULL,

    INDEX idx_threshing_batch_number (batch_number),
    INDEX idx_threshing_date (threshing_date),
    INDEX idx_threshing_status (status),
    INDEX idx_threshing_machine_id (machine_id),
    INDEX idx_threshing_paddy_variety (paddy_variety),
    INDEX idx_threshing_operator (mill_operator_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add comments to columns
ALTER TABLE threshing_records
    MODIFY COLUMN batch_number VARCHAR(50) COMMENT 'Auto-generated unique batch number (TH-YYYYMMDD-XXXX)',
    MODIFY COLUMN paddy_stock_id BINARY(16) COMMENT 'Reference to paddy stock UUID',
    MODIFY COLUMN paddy_variety VARCHAR(100) COMMENT 'Type of paddy being threshed',
    MODIFY COLUMN input_paddy_quantity DECIMAL(10,2) COMMENT 'Input quantity in kg',
    MODIFY COLUMN output_rice_quantity DECIMAL(10,2) COMMENT 'Output rice quantity in kg',
    MODIFY COLUMN wastage_quantity DECIMAL(10,2) COMMENT 'Wastage quantity in kg',
    MODIFY COLUMN threshing_efficiency DECIMAL(5,2) COMMENT 'Efficiency percentage (auto-calculated)',
    MODIFY COLUMN status VARCHAR(20) COMMENT 'PENDING, IN_PROGRESS, COMPLETED, CANCELLED',
    MODIFY COLUMN rice_stock_id BINARY(16) COMMENT 'Reference to created rice stock UUID';

