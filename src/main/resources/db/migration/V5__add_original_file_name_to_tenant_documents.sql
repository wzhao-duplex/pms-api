DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='tenant_documents' AND column_name='original_file_name'
    ) THEN
        ALTER TABLE tenant_documents
        ADD COLUMN original_file_name VARCHAR(255);
    END IF;
END
$$;
