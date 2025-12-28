-- V1__init.sql for MySQL

-- Users table
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    deleted_at TIMESTAMP NULL
);

-- User roles table
CREATE TABLE user_roles (
    user_id BINARY(16) NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'STAFF') NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Refresh tokens
CREATE TABLE refresh_tokens (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    user_id BINARY(16) NOT NULL,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_ip VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user_id (user_id),
    INDEX idx_refresh_tokens_token (token)
);

-- Warehouses
CREATE TABLE warehouses (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    name VARCHAR(100) UNIQUE NOT NULL,
    location VARCHAR(255),
    capacity DECIMAL(15,2) DEFAULT 0 CHECK (capacity >= 0),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Suppliers
CREATE TABLE suppliers (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Customers
CREATE TABLE customers (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Batches/Lots
CREATE TABLE batches (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    type ENUM('PADDY', 'RICE') NOT NULL,
    batch_code VARCHAR(50) NOT NULL,
    batch_date DATE NOT NULL,
    variety VARCHAR(100),
    supplier_id BINARY(16),
    moisture DECIMAL(5,2) CHECK (moisture >= 0 AND moisture <= 100),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    deleted_at TIMESTAMP NULL,
    UNIQUE KEY unique_batch (type, batch_code),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_batches_type (type),
    INDEX idx_batches_batch_code (batch_code)
);

-- Inventory balance
CREATE TABLE inventory_balance (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    warehouse_id BINARY(16) NOT NULL,
    batch_id BINARY(16) NOT NULL,
    type ENUM('PADDY', 'RICE') NOT NULL,
    quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
    unit VARCHAR(10) NOT NULL DEFAULT 'KG',
    version INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BINARY(16),
    updated_by BINARY(16),
    deleted_at TIMESTAMP NULL,
    UNIQUE KEY unique_inventory (warehouse_id, batch_id, type),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_inventory_warehouse_type (warehouse_id, type),
    INDEX idx_inventory_batch (batch_id)
);

-- Stock movements
CREATE TABLE stock_movements (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    movement_type ENUM('INBOUND', 'OUTBOUND', 'TRANSFER', 'ADJUSTMENT', 'PROCESSING') NOT NULL,
    type ENUM('PADDY', 'RICE') NOT NULL,
    quantity DECIMAL(15,2) NOT NULL,
    unit VARCHAR(10) NOT NULL DEFAULT 'KG',
    warehouse_from_id BINARY(16),
    warehouse_to_id BINARY(16),
    batch_id BINARY(16) NOT NULL,
    supplier_id BINARY(16),
    customer_id BINARY(16),
    reference_no VARCHAR(100),
    reason TEXT,
    performed_by BINARY(16) NOT NULL,
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (warehouse_from_id) REFERENCES warehouses(id),
    FOREIGN KEY (warehouse_to_id) REFERENCES warehouses(id),
    FOREIGN KEY (batch_id) REFERENCES batches(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (performed_by) REFERENCES users(id),
    INDEX idx_stock_movements_performed_at (performed_at),
    INDEX idx_stock_movements_warehouse_from (warehouse_from_id),
    INDEX idx_stock_movements_warehouse_to (warehouse_to_id),
    INDEX idx_stock_movements_type (type),
    INDEX idx_stock_movements_movement_type (movement_type),
    INDEX idx_stock_movements_batch (batch_id)
);

-- Processing records
CREATE TABLE processing_records (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    input_batch_id BINARY(16) NOT NULL,
    output_batch_id BINARY(16) NOT NULL,
    warehouse_id BINARY(16) NOT NULL,
    input_qty DECIMAL(15,2) NOT NULL,
    output_qty DECIMAL(15,2) NOT NULL,
    waste_qty DECIMAL(15,2) NOT NULL DEFAULT 0,
    yield_percent DECIMAL(5,2) NOT NULL,
    reference_no VARCHAR(100),
    notes TEXT,
    performed_by BINARY(16) NOT NULL,
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (input_batch_id) REFERENCES batches(id),
    FOREIGN KEY (output_batch_id) REFERENCES batches(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    FOREIGN KEY (performed_by) REFERENCES users(id),
    INDEX idx_processing_records_performed_at (performed_at),
    INDEX idx_processing_records_warehouse (warehouse_id)
);

-- Settings
CREATE TABLE settings (
    id BINARY(16) PRIMARY KEY DEFAULT (UNHEX(REPLACE(UUID(), '-', ''))),
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default settings
INSERT INTO settings (setting_key, setting_value) VALUES
('companyName', 'Rice Mill Management'),
('address', ''),
('contact', ''),
('timezone', 'UTC'),
('lowStockThreshold', '100');

-- Insert admin user (password will be set via application on first run)
-- Password hash for 'admin123' using BCrypt
SET @admin_id = UNHEX(REPLACE(UUID(), '-', ''));
INSERT INTO users (id, username, email, password_hash, full_name, active)
VALUES (
    @admin_id,
    '${ADMIN_USERNAME}',
    '${ADMIN_EMAIL}',
    '$2a$10$dummyHashWillBeReplacedByApplication',
    'System Administrator',
    TRUE
);

-- Assign ADMIN role to the admin user
INSERT INTO user_roles (user_id, role)
VALUES (@admin_id, 'ADMIN');