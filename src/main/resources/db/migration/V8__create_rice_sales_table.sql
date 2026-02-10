-- Create rice_sales table
CREATE TABLE rice_sales (
    id BINARY(16) PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    rice_stock_id BINARY(16) NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
    package_type VARCHAR(50),
    number_of_packages INT,
    price_per_kg DECIMAL(10,2) NOT NULL CHECK (price_per_kg > 0),
    price_per_package DECIMAL(10,2),
    subtotal DECIMAL(12,2) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0 CHECK (discount >= 0 AND discount <= 100),
    discount_amount DECIMAL(12,2) DEFAULT 0,
    tax_rate DECIMAL(5,2) DEFAULT 0,
    tax_amount DECIMAL(12,2) DEFAULT 0,
    delivery_charge DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) NOT NULL,
    balance_amount DECIMAL(12,2) DEFAULT 0,
    customer_id BINARY(16) NOT NULL,
    customer_type VARCHAR(20),
    sale_date DATE NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    delivery_required BOOLEAN DEFAULT FALSE,
    delivery_address TEXT,
    delivery_date DATE,
    vehicle_number VARCHAR(20),
    notes TEXT,
    invoice_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BINARY(16),
    updated_by BINARY(16),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_rice_sale_stock FOREIGN KEY (rice_stock_id) REFERENCES rice_stock(id),
    CONSTRAINT fk_rice_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create indexes
CREATE INDEX idx_rice_sale_invoice ON rice_sales(invoice_number);
CREATE INDEX idx_rice_sale_date ON rice_sales(sale_date);
CREATE INDEX idx_rice_sale_customer ON rice_sales(customer_id);
CREATE INDEX idx_rice_sale_payment_status ON rice_sales(payment_status);
CREATE INDEX idx_rice_sale_batch ON rice_sales(batch_number);
CREATE INDEX idx_rice_sale_customer_type ON rice_sales(customer_type);

