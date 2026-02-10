-- Create payment_transactions table
CREATE TABLE payment_transactions (
    id BINARY(16) PRIMARY KEY,
    sale_id BINARY(16) NOT NULL,
    sale_type VARCHAR(20) NOT NULL,
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(20) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    reference_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BINARY(16),
    updated_by BINARY(16),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_payment_sale ON payment_transactions(sale_id, sale_type);
CREATE INDEX idx_payment_date ON payment_transactions(payment_date);
CREATE INDEX idx_payment_method ON payment_transactions(payment_method);

