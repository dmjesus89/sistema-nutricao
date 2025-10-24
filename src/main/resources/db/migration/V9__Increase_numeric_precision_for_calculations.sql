-- Migration to increase numeric precision for BMR, TDEE, and calorie calculations
-- to prevent numeric field overflow errors

-- Increase precision for basal_metabolic_rate from NUMERIC(7,2) to NUMERIC(8,2)
-- This allows values up to 999,999.99 instead of 99,999.99
ALTER TABLE user_profiles
    ALTER COLUMN basal_metabolic_rate TYPE NUMERIC(8, 2);

-- Increase precision for total_daily_energy_expenditure from NUMERIC(7,2) to NUMERIC(8,2)
ALTER TABLE user_profiles
    ALTER COLUMN total_daily_energy_expenditure TYPE NUMERIC(8, 2);

-- Increase precision for daily_calories from NUMERIC(7,2) to NUMERIC(8,2)
ALTER TABLE user_profiles
    ALTER COLUMN daily_calories TYPE NUMERIC(8, 2);

-- Increase precision for daily_water_intake from NUMERIC(7,2) to NUMERIC(8,2)
-- for consistency and to support larger water intake values if needed
ALTER TABLE user_profiles
    ALTER COLUMN daily_water_intake TYPE NUMERIC(8, 2);

-- Add comment explaining the change
COMMENT ON COLUMN user_profiles.basal_metabolic_rate IS 'BMR in kcal/day - Precision increased to NUMERIC(8,2) to support values up to 999,999.99';
COMMENT ON COLUMN user_profiles.total_daily_energy_expenditure IS 'TDEE in kcal/day - Precision increased to NUMERIC(8,2) to support values up to 999,999.99';
COMMENT ON COLUMN user_profiles.daily_calories IS 'Daily calorie target in kcal - Precision increased to NUMERIC(8,2) to support values up to 999,999.99';
COMMENT ON COLUMN user_profiles.daily_water_intake IS 'Daily water intake in ml - Precision increased to NUMERIC(8,2) to support values up to 999,999.99';
