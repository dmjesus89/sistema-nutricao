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
-- 3. user_supplements table structure is already correct in V1 (no changes needed)
-- ============================================================================
-- Skipping - user_supplements table created with correct structure in V1

-- ============================================================================
-- 4. Clean up old food preference types (keep only FAVORITE)
-- ============================================================================
-- Delete preferences that are not FAVORITE
DELETE FROM user_food_preferences
WHERE preference_type NOT IN ('FAVORITE');

-- ============================================================================
-- 5. user_supplements no longer has preference_type (frequency-based model)
-- ============================================================================
-- Skipping - preference_type removed in new model

-- ============================================================================
-- 6. Add SUPPLEMENT_REMINDER to email_queue if email_type enum exists
-- ============================================================================
-- Note: If you're using PostgreSQL ENUM, you'll need to add the new type
-- This is for PostgreSQL:
-- ALTER TYPE email_type ADD VALUE IF NOT EXISTS 'SUPPLEMENT_REMINDER';

-- For other databases or if using VARCHAR, no action needed as the enum is in Java

-- ============================================================================
-- 7. Indexes already created in V1 for user_supplements
-- ============================================================================
-- Skipping - indexes already created in V1

-- ============================================================================
-- Migration complete
-- ============================================================================
