-- Create customers table
CREATE TABLE customers (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    customer_type VARCHAR(20) DEFAULT 'RETAIL',
    credit_limit DECIMAL(12,2) DEFAULT 0,
    outstanding_balance DECIMAL(12,2) DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by BINARY(16),
    updated_by BINARY(16),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_customer_contact ON customers(contact);
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_customer_type ON customers(customer_type);

