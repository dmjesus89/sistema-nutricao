-- ============================================================================
-- V11: Add support for multiple dosage times per supplement
-- ============================================================================
-- Allows users to take the same supplement multiple times per day
-- (e.g., morning and evening, or 3x daily for some vitamins)
-- ============================================================================

-- Create schedules table
CREATE TABLE user_supplement_schedules (
    id BIGSERIAL PRIMARY KEY,
    user_supplement_id BIGINT NOT NULL,
    dosage_time TIME NOT NULL,
    label VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_supplement FOREIGN KEY (user_supplement_id)
        REFERENCES user_supplements(id) ON DELETE CASCADE
);

-- Index for faster lookups
CREATE INDEX idx_user_supplement_schedules_supplement
    ON user_supplement_schedules(user_supplement_id);

-- Migrate existing dosage_time data to schedules table
INSERT INTO user_supplement_schedules (user_supplement_id, dosage_time, label, created_at)
SELECT id, dosage_time, 'Default', NOW()
FROM user_supplements
WHERE dosage_time IS NOT NULL;

-- Note: We're keeping the dosage_time column in user_supplements for backward compatibility
-- It will be used as the primary/default time if no schedules exist
-- New API will use schedules table for multiple times per day
