-- RentMIS Database Initialization Script
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS rentmis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rentmis;

-- Create application user
CREATE USER IF NOT EXISTS 'rentmis_user'@'localhost' IDENTIFIED BY 'RentMIS@2024!';
GRANT ALL PRIVILEGES ON rentmis.* TO 'rentmis_user'@'localhost';
FLUSH PRIVILEGES;

-- =============================================================================
-- USERS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    national_id VARCHAR(50),
    role ENUM('ADMIN','LANDLORD','TENANT') NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    is_verified TINYINT(1) DEFAULT 0,
    profile_image VARCHAR(500),
    address VARCHAR(500),
    refresh_token VARCHAR(500),
    refresh_token_expiry DATETIME,
    password_reset_token VARCHAR(200),
    password_reset_expiry DATETIME,
    last_login DATETIME,
    failed_login_attempts INT DEFAULT 0,
    locked_until DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY idx_users_email (email),
    INDEX idx_users_phone (phone),
    INDEX idx_users_role (role),
    INDEX idx_users_nid (national_id),
    INDEX idx_users_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- PROPERTIES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS properties (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    district VARCHAR(100),
    sector VARCHAR(100),
    cell VARCHAR(100),
    description TEXT,
    property_type VARCHAR(50),
    total_units INT DEFAULT 0,
    image_url VARCHAR(500),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    is_active TINYINT(1) DEFAULT 1,
    landlord_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id),
    INDEX idx_properties_landlord (landlord_id),
    INDEX idx_properties_status (is_active),
    CONSTRAINT fk_properties_landlord FOREIGN KEY (landlord_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- UNITS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS units (
    id BIGINT NOT NULL AUTO_INCREMENT,
    unit_number VARCHAR(50) NOT NULL,
    floor_number INT,
    unit_type VARCHAR(50),
    rent_amount DECIMAL(12,2) NOT NULL,
    deposit_amount DECIMAL(12,2),
    area_sqm DECIMAL(8,2),
    num_bedrooms INT,
    num_bathrooms INT,
    status ENUM('AVAILABLE','OCCUPIED','MAINTENANCE','RESERVED') NOT NULL DEFAULT 'AVAILABLE',
    amenities TEXT,
    is_active TINYINT(1) DEFAULT 1,
    property_id BIGINT NOT NULL,
    current_tenant_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id),
    INDEX idx_units_property (property_id),
    INDEX idx_units_status (status),
    INDEX idx_units_tenant (current_tenant_id),
    UNIQUE KEY uk_unit_property_number (property_id, unit_number),
    CONSTRAINT fk_units_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_units_tenant FOREIGN KEY (current_tenant_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- CONTRACTS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS contracts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    contract_number VARCHAR(50) NOT NULL,
    tenant_id BIGINT NOT NULL,
    landlord_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_rent DECIMAL(12,2) NOT NULL,
    deposit_amount DECIMAL(12,2),
    status ENUM('DRAFT','PENDING_SIGNATURE','ACTIVE','EXPIRED','TERMINATED','RENEWED') NOT NULL DEFAULT 'DRAFT',
    terms_conditions LONGTEXT,
    special_clauses TEXT,
    landlord_signed_at DATETIME,
    tenant_signed_at DATETIME,
    landlord_signature_ip VARCHAR(45),
    tenant_signature_ip VARCHAR(45),
    contract_hash VARCHAR(64),
    blockchain_tx_hash VARCHAR(100),
    blockchain_network VARCHAR(50),
    blockchain_timestamp DATETIME,
    blockchain_block_number BIGINT,
    terminated_at DATETIME,
    termination_reason TEXT,
    pdf_url VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY uk_contract_number (contract_number),
    INDEX idx_contracts_tenant (tenant_id),
    INDEX idx_contracts_landlord (landlord_id),
    INDEX idx_contracts_unit (unit_id),
    INDEX idx_contracts_status (status),
    INDEX idx_contracts_hash (contract_hash),
    CONSTRAINT fk_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES users(id),
    CONSTRAINT fk_contracts_landlord FOREIGN KEY (landlord_id) REFERENCES users(id),
    CONSTRAINT fk_contracts_unit FOREIGN KEY (unit_id) REFERENCES units(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- PAYMENTS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    reference_number VARCHAR(100) NOT NULL,
    tenant_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    contract_id BIGINT,
    amount DECIMAL(12,2) NOT NULL,
    penalty_amount DECIMAL(12,2) DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'RWF',
    status ENUM('PENDING','PROCESSING','COMPLETED','FAILED','REFUNDED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    payment_period_month INT,
    payment_period_year INT,
    due_date DATE,
    paid_at DATETIME,
    glspay_transaction_id VARCHAR(200),
    glspay_checkout_url VARCHAR(1000),
    glspay_webhook_data TEXT,
    glspay_signature_verified TINYINT(1) DEFAULT 0,
    notes TEXT,
    idempotency_key VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY uk_reference_number (reference_number),
    UNIQUE KEY uk_idempotency_key (idempotency_key),
    INDEX idx_payments_tenant (tenant_id),
    INDEX idx_payments_unit (unit_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_reference (reference_number),
    INDEX idx_payments_period (payment_period_month, payment_period_year),
    INDEX idx_payments_glspay_tx (glspay_transaction_id),
    CONSTRAINT fk_payments_tenant FOREIGN KEY (tenant_id) REFERENCES users(id),
    CONSTRAINT fk_payments_unit FOREIGN KEY (unit_id) REFERENCES units(id),
    CONSTRAINT fk_payments_contract FOREIGN KEY (contract_id) REFERENCES contracts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- INVOICES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    invoice_number VARCHAR(100) NOT NULL,
    ebm_invoice_number VARCHAR(100),
    issued_at DATETIME NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    tax_amount DECIMAL(12,2) DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'RWF',
    qr_code VARCHAR(2000),
    pdf_url VARCHAR(500),
    ebm_status VARCHAR(30),
    ebm_response TEXT,
    ebm_submitted_at DATETIME,
    ebm_retry_count INT DEFAULT 0,
    verification_url VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY uk_invoice_number (invoice_number),
    UNIQUE KEY uk_payment_id (payment_id),
    INDEX idx_invoices_ebm (ebm_invoice_number),
    CONSTRAINT fk_invoices_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- AUDIT LOGS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT,
    user_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    status VARCHAR(20),
    error_message TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- SEED DATA: Default Admin User
-- Password: Admin@2024! (BCrypt encoded)
-- =============================================================================
INSERT IGNORE INTO users (first_name, last_name, email, password, role, is_active, is_verified)
VALUES (
    'System', 'Administrator',
    'admin@rentmis.rw',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/RrMJMI5q6',
    'ADMIN', 1, 1
);
