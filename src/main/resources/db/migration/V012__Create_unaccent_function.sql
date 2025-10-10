-- Create function to remove accents for case-insensitive and accent-insensitive search
-- This allows users to search with or without accents (e.g., "açai" or "acai")

-- First, try to create the unaccent extension if available
-- If not available, we'll create a custom function
DO $$
BEGIN
    -- Try to create the unaccent extension (available in PostgreSQL with contrib modules)
    CREATE EXTENSION IF NOT EXISTS unaccent;
EXCEPTION
    WHEN OTHERS THEN
        -- If extension is not available, we'll use the custom function below
        NULL;
END $$;

-- Create a custom function to remove accents if the extension is not available
-- This function handles Portuguese accents
CREATE OR REPLACE FUNCTION remove_accents(text)
RETURNS text AS $$
BEGIN
    RETURN translate(
        $1,
        'áàâãäéèêëíìîïóòôõöúùûüçñÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇÑ',
        'aaaaaeeeeiiiiooooouuuucnAAAAAEEEEIIIIOOOOOUUUUCN'
    );
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Create indexes for faster search on foods table
CREATE INDEX IF NOT EXISTS idx_foods_name_unaccent
    ON foods (remove_accents(LOWER(name)));

CREATE INDEX IF NOT EXISTS idx_foods_category_unaccent
    ON foods (remove_accents(LOWER(category)));

-- Create indexes for faster search on supplements table
CREATE INDEX IF NOT EXISTS idx_supplements_name_unaccent
    ON supplements (remove_accents(LOWER(name)));

CREATE INDEX IF NOT EXISTS idx_supplements_category_unaccent
    ON supplements (remove_accents(LOWER(category)));
