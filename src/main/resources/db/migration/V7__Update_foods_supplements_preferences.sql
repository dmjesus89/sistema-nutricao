-- Migration V7: Update foods, supplements and user preferences
-- Remove verified fields and update preference types

-- ============================================================================
-- 1. Remove 'verified' column from foods table
-- ============================================================================
ALTER TABLE foods DROP COLUMN IF EXISTS verified;

-- ============================================================================
-- 2. Remove 'verified' column from supplements table
-- ============================================================================
ALTER TABLE supplements DROP COLUMN IF EXISTS verified;

-- ============================================================================
-- 3. Add time routine fields to user_supplement_preferences table
-- ============================================================================
ALTER TABLE user_supplement_preferences
    ADD COLUMN IF NOT EXISTS dosage_time TIME,
    ADD COLUMN IF NOT EXISTS frequency VARCHAR(20),
    ADD COLUMN IF NOT EXISTS days_of_week VARCHAR(100),
    ADD COLUMN IF NOT EXISTS email_reminder_enabled BOOLEAN DEFAULT FALSE NOT NULL;

-- ============================================================================
-- 4. Clean up old food preference types (keep only FAVORITE)
-- ============================================================================
-- Delete preferences that are not FAVORITE
DELETE FROM user_food_preferences
WHERE preference_type NOT IN ('FAVORITE');

-- ============================================================================
-- 5. Clean up old supplement preference types (keep only CURRENTLY_USING)
-- ============================================================================
-- Delete preferences that are not CURRENTLY_USING
DELETE FROM user_supplement_preferences
WHERE preference_type NOT IN ('CURRENTLY_USING');

-- ============================================================================
-- 6. Add SUPPLEMENT_REMINDER to email_queue if email_type enum exists
-- ============================================================================
-- Note: If you're using PostgreSQL ENUM, you'll need to add the new type
-- This is for PostgreSQL:
-- ALTER TYPE email_type ADD VALUE IF NOT EXISTS 'SUPPLEMENT_REMINDER';

-- For other databases or if using VARCHAR, no action needed as the enum is in Java

-- ============================================================================
-- 7. Add indexes for performance on new columns
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_supplement_pref_email_reminder
    ON user_supplement_preferences(email_reminder_enabled)
    WHERE email_reminder_enabled = TRUE;

CREATE INDEX IF NOT EXISTS idx_supplement_pref_dosage_time
    ON user_supplement_preferences(dosage_time)
    WHERE dosage_time IS NOT NULL;

-- ============================================================================
-- Migration complete
-- ============================================================================
