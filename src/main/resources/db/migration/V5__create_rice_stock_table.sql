-- Create rice_stock table
CREATE TABLE rice_stock (
    id BINARY(16) PRIMARY KEY,
    rice_type VARCHAR(100) NOT NULL,
    variety VARCHAR(100),
    quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
    package_type VARCHAR(50),
    number_of_packages INT,
    price_per_kg DECIMAL(10,2) NOT NULL CHECK (price_per_kg > 0),
    processing_date DATE NOT NULL,
    expiry_date DATE,
    warehouse_location VARCHAR(255),
    quality_grade VARCHAR(20),
    batch_number VARCHAR(50) UNIQUE NOT NULL,
    source_paddy_batch VARCHAR(50),
    notes TEXT,
    total_value DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BINARY(16),
    updated_by BINARY(16),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_rice_stock_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_rice_stock_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CONSTRAINT fk_rice_source_paddy FOREIGN KEY (source_paddy_batch) REFERENCES paddy_stock(batch_number),
    CONSTRAINT chk_rice_expiry_date CHECK (expiry_date IS NULL OR expiry_date > processing_date)
);

-- Create indexes for faster queries
CREATE INDEX idx_rice_stock_batch_number ON rice_stock(batch_number);
CREATE INDEX idx_rice_stock_processing_date ON rice_stock(processing_date);
CREATE INDEX idx_rice_stock_rice_type ON rice_stock(rice_type);
CREATE INDEX idx_rice_stock_warehouse ON rice_stock(warehouse_location);
CREATE INDEX idx_rice_stock_source_paddy ON rice_stock(source_paddy_batch);
CREATE INDEX idx_rice_stock_active ON rice_stock(active);

