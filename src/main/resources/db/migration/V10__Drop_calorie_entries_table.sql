-- Migration to remove unused calorie tracking functionality
-- Removes the calorie_entries table and all related constraints and indexes

-- Drop all foreign key constraints first
ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS fk_calorie_entries_user;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS fk_calorie_entries_food;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS fk_calorie_entries_meal;

-- Drop all check constraints
ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_entry_type;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_calories_positive;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_carbs_non_negative;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_protein_non_negative;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_fat_non_negative;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_quantity_positive;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_food_logic;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_meal_logic;

ALTER TABLE calorie_entries
    DROP CONSTRAINT IF EXISTS chk_calorie_entries_manual_logic;

-- Drop all indexes
DROP INDEX IF EXISTS idx_calorie_entries_user_date;
DROP INDEX IF EXISTS idx_calorie_entries_user_consumed_at;
DROP INDEX IF EXISTS idx_calorie_entries_entry_type;
DROP INDEX IF EXISTS idx_calorie_entries_food;
DROP INDEX IF EXISTS idx_calorie_entries_meal;

-- Finally, drop the table
DROP TABLE IF EXISTS calorie_entries;

-- Add comment explaining the removal
COMMENT ON SCHEMA public IS 'Removed calorie_entries table - feature was not implemented in frontend and all related backend code has been removed';
