-- V8: Add preferredLocale column to users table for internationalization support

ALTER TABLE users ADD COLUMN IF NOT EXISTS preferred_locale VARCHAR(5) DEFAULT 'en';

COMMENT ON COLUMN users.preferred_locale IS 'User preferred language: en (English), es (Spanish), pt (Portuguese)';

-- Update existing users to have default locale
UPDATE users SET preferred_locale = 'en' WHERE preferred_locale IS NULL;
