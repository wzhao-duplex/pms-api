-- =========================================
-- PMS INITIAL DATABASE SCHEMA
-- Version: V1
-- =========================================

-- Enable UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ======================
-- USERS
-- ======================
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ======================
-- ORGANIZATIONS
-- ======================
CREATE TABLE organizations (
    org_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    org_name VARCHAR(255) NOT NULL,
    owner_user_id UUID NOT NULL,
    subscription_start DATE,
    subscription_end DATE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_org_owner
        FOREIGN KEY (owner_user_id)
        REFERENCES users(user_id)
);

-- ======================
-- PROPERTIES
-- ======================
CREATE TABLE properties (
    property_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    org_id UUID NOT NULL,

    address VARCHAR(500) NOT NULL,
    city VARCHAR(100),
    province VARCHAR(50),
    postal_code VARCHAR(20),

    property_type VARCHAR(50),
    ownership_percent NUMERIC(5,2) DEFAULT 100.00,
    self_use_percent NUMERIC(5,2) DEFAULT 0.00,
    management_company VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_property_org
        FOREIGN KEY (org_id)
        REFERENCES organizations(org_id)
);

-- ======================
-- TENANTS
-- ======================
CREATE TABLE tenants (
    tenant_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    property_id UUID NOT NULL,

    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255),

    lease_start DATE,
    lease_end DATE,
    monthly_rent NUMERIC(12,2),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tenant_property
        FOREIGN KEY (property_id)
        REFERENCES properties(property_id)
);

-- ======================
-- TENANT DOCUMENTS
-- ======================
CREATE TABLE tenant_documents (
    document_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL,

    document_type VARCHAR(50),
    s3_key VARCHAR(500) NOT NULL,
    encrypted_key VARCHAR(500) NOT NULL,

    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_doc_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(tenant_id)
);

-- ======================
-- PROPERTY INCOME
-- ======================
CREATE TABLE property_income (
    income_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    property_id UUID NOT NULL,

    income_type VARCHAR(50) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    income_date DATE NOT NULL,
    tax_year INT NOT NULL,

    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_income_property
        FOREIGN KEY (property_id)
        REFERENCES properties(property_id)
);

-- ======================
-- PROPERTY EXPENSES
-- ======================
CREATE TABLE property_expenses (
    expense_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    property_id UUID NOT NULL,

    expense_type VARCHAR(50) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    expense_date DATE NOT NULL,
    tax_year INT NOT NULL,

    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_expense_property
        FOREIGN KEY (property_id)
        REFERENCES properties(property_id)
);

-- ======================
-- MORTGAGE PAYMENTS
-- ======================
CREATE TABLE mortgage_payments (
    payment_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    property_id UUID NOT NULL,

    payment_month DATE NOT NULL,
    principal_amount NUMERIC(12,2) NOT NULL,
    interest_amount NUMERIC(12,2) NOT NULL,
    tax_year INT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mortgage_property
        FOREIGN KEY (property_id)
        REFERENCES properties(property_id)
);

-- ======================
-- MAINTENANCE RECORDS
-- ======================
CREATE TABLE maintenance_records (
    maintenance_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    property_id UUID NOT NULL,

    category VARCHAR(50),
    description VARCHAR(1000),
    cost NUMERIC(12,2),
    contractor_name VARCHAR(255),
    contractor_phone VARCHAR(50),
    service_date DATE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_maintenance_property
        FOREIGN KEY (property_id)
        REFERENCES properties(property_id)
);

-- ======================
-- SUBSCRIPTIONS
-- ======================
CREATE TABLE subscriptions (
    subscription_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    org_id UUID NOT NULL,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_subscription_org
        FOREIGN KEY (org_id)
        REFERENCES organizations(org_id)
);
