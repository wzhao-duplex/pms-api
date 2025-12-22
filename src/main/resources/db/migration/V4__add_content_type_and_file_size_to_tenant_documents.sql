DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='tenant_documents' AND column_name='content_type'
    ) THEN
        ALTER TABLE tenant_documents
        ADD COLUMN content_type VARCHAR(100) NOT NULL DEFAULT 'application/octet-stream';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='tenant_documents' AND column_name='file_size'
    ) THEN
        ALTER TABLE tenant_documents
        ADD COLUMN file_size BIGINT NOT NULL DEFAULT 0;
    END IF;
END
$$;
