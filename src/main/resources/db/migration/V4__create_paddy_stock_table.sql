-- Create paddy_stock table
CREATE TABLE paddy_stock (
    id BINARY(16) PRIMARY KEY,
    paddy_type VARCHAR(100) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
    price_per_kg DECIMAL(10,2) NOT NULL CHECK (price_per_kg > 0),
    supplier VARCHAR(255) NOT NULL,
    supplier_contact VARCHAR(20),
    purchase_date DATE NOT NULL,
    warehouse_location VARCHAR(255),
    moisture_content DECIMAL(5,2) CHECK (moisture_content >= 0 AND moisture_content <= 100),
    quality_grade VARCHAR(10),
    batch_number VARCHAR(50) UNIQUE NOT NULL,
    notes TEXT,
    total_value DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BINARY(16),
    updated_by BINARY(16),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_paddy_stock_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_paddy_stock_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);

-- Create index for faster queries
CREATE INDEX idx_paddy_stock_batch_number ON paddy_stock(batch_number);
CREATE INDEX idx_paddy_stock_purchase_date ON paddy_stock(purchase_date);
CREATE INDEX idx_paddy_stock_paddy_type ON paddy_stock(paddy_type);
CREATE INDEX idx_paddy_stock_warehouse ON paddy_stock(warehouse_location);
CREATE INDEX idx_paddy_stock_active ON paddy_stock(active);

