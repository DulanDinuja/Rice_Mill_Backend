-- V2__add_user_id_and_mobile.sql
-- Add id_number and mobile_number columns to users table

ALTER TABLE users
ADD COLUMN id_number VARCHAR(50) NULL AFTER full_name,
ADD COLUMN mobile_number VARCHAR(20) NULL AFTER id_number;

