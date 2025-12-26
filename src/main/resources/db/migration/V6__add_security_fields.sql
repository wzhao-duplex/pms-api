-- Add password hash for security
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255);

-- Link users to an organization (Staff/Agents belong to an Org)
ALTER TABLE users ADD COLUMN org_id UUID;

ALTER TABLE users 
    ADD CONSTRAINT fk_user_org 
    FOREIGN KEY (org_id) 
    REFERENCES organizations(org_id);

-- Add index for performance
CREATE INDEX idx_users_email ON users(email);