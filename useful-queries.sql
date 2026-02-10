-- Rice Mill Database - Useful Queries
-- =====================================

-- 1. SELECT THE DATABASE FIRST (Important!)
USE ricemill_db;

-- =====================================
-- USER QUERIES
-- =====================================

-- View all users
SELECT * FROM users;

-- View active users with details
SELECT
    BIN_TO_UUID(id) as user_id,
    username,
    email,
    full_name,
    id_number,
    mobile_number,
    active,
    created_at,
    updated_at
FROM users
WHERE deleted_at IS NULL
ORDER BY created_at DESC;

-- View user roles
SELECT
    BIN_TO_UUID(u.id) as user_id,
    u.username,
    u.email,
    ur.role
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
WHERE u.deleted_at IS NULL
ORDER BY u.username;

-- Check for duplicate usernames (should be empty)
SELECT
    username,
    COUNT(*) as count
FROM users
GROUP BY username
HAVING count > 1;

-- Check for duplicate emails (should be empty)
SELECT
    email,
    COUNT(*) as count
FROM users
GROUP BY email
HAVING count > 1;

-- Check for duplicate ID numbers (should be empty)
SELECT
    id_number,
    COUNT(*) as count
FROM users
WHERE id_number IS NOT NULL
GROUP BY id_number
HAVING count > 1;

-- Find user by username
SELECT
    BIN_TO_UUID(id) as user_id,
    username,
    email,
    full_name,
    id_number,
    mobile_number,
    active
FROM users
WHERE username = 'testuser';

-- Find user by email
SELECT
    BIN_TO_UUID(id) as user_id,
    username,
    email,
    full_name,
    id_number,
    mobile_number,
    active
FROM users
WHERE email = 'testuser@example.com';

-- Find user by ID number
SELECT
    BIN_TO_UUID(id) as user_id,
    username,
    email,
    full_name,
    id_number,
    mobile_number,
    active
FROM users
WHERE id_number = '123456789V';

-- Count users by role
SELECT
    ur.role,
    COUNT(*) as user_count
FROM user_roles ur
GROUP BY ur.role;

-- View recently registered users (last 7 days)
SELECT
    BIN_TO_UUID(id) as user_id,
    username,
    email,
    full_name,
    created_at
FROM users
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
    AND deleted_at IS NULL
ORDER BY created_at DESC;

-- =====================================
-- CUSTOMER QUERIES
-- =====================================

-- View all customers
SELECT
    BIN_TO_UUID(id) as customer_id,
    name,
    phone,
    address,
    active,
    created_at
FROM customers
WHERE deleted_at IS NULL
ORDER BY created_at DESC;

-- =====================================
-- SUPPLIER QUERIES
-- =====================================

-- View all suppliers
SELECT
    BIN_TO_UUID(id) as supplier_id,
    name,
    phone,
    address,
    active,
    created_at
FROM suppliers
WHERE deleted_at IS NULL
ORDER BY created_at DESC;

-- =====================================
-- WAREHOUSE QUERIES
-- =====================================

-- View all warehouses
SELECT
    BIN_TO_UUID(id) as warehouse_id,
    name,
    location,
    capacity,
    active,
    created_at
FROM warehouses
WHERE deleted_at IS NULL
ORDER BY created_at DESC;

-- =====================================
-- REFRESH TOKEN QUERIES
-- =====================================

-- View active refresh tokens
SELECT
    BIN_TO_UUID(rt.id) as token_id,
    u.username,
    u.email,
    rt.token,
    rt.expires_at,
    rt.created_at
FROM refresh_tokens rt
JOIN users u ON rt.user_id = u.id
WHERE rt.revoked_at IS NULL
    AND rt.expires_at > NOW()
ORDER BY rt.created_at DESC;

-- View expired refresh tokens
SELECT
    BIN_TO_UUID(rt.id) as token_id,
    u.username,
    rt.expires_at,
    rt.created_at
FROM refresh_tokens rt
JOIN users u ON rt.user_id = u.id
WHERE rt.expires_at <= NOW()
ORDER BY rt.expires_at DESC;

-- =====================================
-- DATABASE STATISTICS
-- =====================================

-- Count all tables
SELECT
    'Users' as entity, COUNT(*) as count FROM users WHERE deleted_at IS NULL
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers WHERE deleted_at IS NULL
UNION ALL
SELECT 'Suppliers', COUNT(*) FROM suppliers WHERE deleted_at IS NULL
UNION ALL
SELECT 'Warehouses', COUNT(*) FROM warehouses WHERE deleted_at IS NULL
UNION ALL
SELECT 'Batches', COUNT(*) FROM batches WHERE deleted_at IS NULL
UNION ALL
SELECT 'Stock Movements', COUNT(*) FROM stock_movements;

-- =====================================
-- CLEANUP QUERIES (Use with caution!)
-- =====================================

-- Delete expired refresh tokens (older than 30 days)
-- DELETE FROM refresh_tokens WHERE expires_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Soft delete inactive users (Use carefully!)
-- UPDATE users SET deleted_at = NOW() WHERE active = FALSE;

-- =====================================
-- TESTING QUERIES
-- =====================================

-- Create test user (for development only)
-- INSERT INTO users (id, username, email, password_hash, full_name, id_number, mobile_number, active, created_at, updated_at)
-- VALUES (
--     UNHEX(REPLACE(UUID(), '-', '')),
--     'testuser',
--     'testuser@example.com',
--     '$2a$10$abcdefghijklmnopqrstuvwxyz', -- Replace with actual bcrypt hash
--     'Test User',
--     'TEST123',
--     '+94771234567',
--     TRUE,
--     NOW(),
--     NOW()
-- );

-- Delete test user
-- DELETE FROM users WHERE username = 'testuser';

-- =====================================
-- MIGRATION STATUS
-- =====================================

-- Check Flyway migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- View latest migration
SELECT
    installed_rank,
    version,
    description,
    type,
    script,
    installed_on,
    success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 1;

