ALTER TABLE users ADD COLUMN verification_code VARCHAR(10);
ALTER TABLE users ADD COLUMN verification_expiry TIMESTAMP;

-- Update existing users to be verified so they don't get locked out
UPDATE users SET status = 'ACTIVE' WHERE status IS NULL;