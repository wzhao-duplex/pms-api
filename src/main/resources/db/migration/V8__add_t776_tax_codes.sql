-- 1. Create Tax Codes Reference Table
CREATE TABLE tax_codes (
    code VARCHAR(10) PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);

-- 2. Seed Data (Standard CRA T776 Lines)
INSERT INTO tax_codes (code, description) VALUES 
('8521', 'Advertising'),
('8690', 'Insurance'),
('8710', 'Interest and bank charges'),
('8860', 'Professional fees (legal and accounting)'),
('8871', 'Management and administration fees'),
('8960', 'Repairs and maintenance'),
('9060', 'Salaries, wages, and benefits'),
('9180', 'Property taxes'),
('9200', 'Travel'),
('9220', 'Utilities'),
('9281', 'Motor vehicle expenses'),
('9999', 'Other expenses');

-- 3. Update Property Expenses Table
ALTER TABLE property_expenses ADD COLUMN tax_code VARCHAR(10);

ALTER TABLE property_expenses 
    ADD CONSTRAINT fk_expense_tax_code
    FOREIGN KEY (tax_code) 
    REFERENCES tax_codes(code);

-- 4. Auto-map existing data (Best guess based on your previous enums)
-- Adjust strings 'TAX', 'INSURANCE' based on what you actually saved in DB
UPDATE property_expenses SET tax_code = '9180' WHERE expense_type = 'TAX';
UPDATE property_expenses SET tax_code = '8690' WHERE expense_type = 'INSURANCE';
UPDATE property_expenses SET tax_code = '8960' WHERE expense_type = 'MAINTENANCE';
UPDATE property_expenses SET tax_code = '8871' WHERE expense_type = 'MANAGEMENT_FEE';
UPDATE property_expenses SET tax_code = '9220' WHERE expense_type = 'UTILITIES';