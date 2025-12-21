-- V1__init.sql

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

-- Roles (using enum approach)
CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'STAFF');

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role user_role NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Refresh tokens
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_ip VARCHAR(50)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- Warehouses
CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    location VARCHAR(255),
    capacity DECIMAL(15,2) DEFAULT 0 CHECK (capacity >= 0),
    notes TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    deleted_at TIMESTAMP
);

-- Suppliers
CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    deleted_at TIMESTAMP
);

-- Customers
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    deleted_at TIMESTAMP
);

-- Product type enum
CREATE TYPE product_type AS ENUM ('PADDY', 'RICE');

-- Batches/Lots
CREATE TABLE batches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type product_type NOT NULL,
    batch_code VARCHAR(50) NOT NULL,
    batch_date DATE NOT NULL,
    variety VARCHAR(100),
    supplier_id UUID REFERENCES suppliers(id),
    moisture DECIMAL(5,2) CHECK (moisture >= 0 AND moisture <= 100),
    notes TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    deleted_at TIMESTAMP,
    UNIQUE (type, batch_code)
);

CREATE INDEX idx_batches_type ON batches(type);
CREATE INDEX idx_batches_batch_code ON batches(batch_code);

-- Inventory balance
CREATE TABLE inventory_balance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    batch_id UUID NOT NULL REFERENCES batches(id),
    type product_type NOT NULL,
    quantity DECIMAL(15,2) NOT NULL DEFAULT 0,
    unit VARCHAR(10) NOT NULL DEFAULT 'KG',
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    UNIQUE (warehouse_id, batch_id, type)
);

CREATE INDEX idx_inventory_warehouse_type ON inventory_balance(warehouse_id, type);
CREATE INDEX idx_inventory_batch ON inventory_balance(batch_id);

-- Movement type enum
CREATE TYPE movement_type AS ENUM ('INBOUND', 'OUTBOUND', 'TRANSFER', 'ADJUSTMENT', 'PROCESSING');

-- Stock movements
CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    movement_type movement_type NOT NULL,
    type product_type NOT NULL,
    quantity DECIMAL(15,2) NOT NULL,
    unit VARCHAR(10) NOT NULL DEFAULT 'KG',
    warehouse_from_id UUID REFERENCES warehouses(id),
    warehouse_to_id UUID REFERENCES warehouses(id),
    batch_id UUID NOT NULL REFERENCES batches(id),
    supplier_id UUID REFERENCES suppliers(id),
    customer_id UUID REFERENCES customers(id),
    reference_no VARCHAR(100),
    reason TEXT,
    performed_by UUID NOT NULL REFERENCES users(id),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_movements_performed_at ON stock_movements(performed_at);
CREATE INDEX idx_stock_movements_warehouse_from ON stock_movements(warehouse_from_id);
CREATE INDEX idx_stock_movements_warehouse_to ON stock_movements(warehouse_to_id);
CREATE INDEX idx_stock_movements_type ON stock_movements(type);
CREATE INDEX idx_stock_movements_movement_type ON stock_movements(movement_type);
CREATE INDEX idx_stock_movements_batch ON stock_movements(batch_id);

-- Processing records
CREATE TABLE processing_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    input_batch_id UUID NOT NULL REFERENCES batches(id),
    output_batch_id UUID NOT NULL REFERENCES batches(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    input_qty DECIMAL(15,2) NOT NULL,
    output_qty DECIMAL(15,2) NOT NULL,
    waste_qty DECIMAL(15,2) NOT NULL DEFAULT 0,
    yield_percent DECIMAL(5,2) NOT NULL,
    reference_no VARCHAR(100),
    notes TEXT,
    performed_by UUID NOT NULL REFERENCES users(id),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_processing_records_performed_at ON processing_records(performed_at);
CREATE INDEX idx_processing_records_warehouse ON processing_records(warehouse_id);

-- Settings
CREATE TABLE settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
INSERT INTO users (id, username, email, password_hash, full_name, active)
VALUES (
    uuid_generate_v4(),
    '${ADMIN_USERNAME}',
    '${ADMIN_EMAIL}',
    '$2a$10$dummyHashWillBeReplacedByApplication',
    'System Administrator',
    true
);

-- Assign ADMIN role to the admin user
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN'::user_role FROM users WHERE username = '${ADMIN_USERNAME}';
