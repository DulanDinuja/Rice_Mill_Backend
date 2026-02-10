-- V3__add_unique_constraint_id_number.sql
-- Add unique constraint to id_number column

ALTER TABLE users
ADD UNIQUE KEY unique_id_number (id_number);

