-- V011__Refactor_meals_to_templates.sql
-- Migration to refactor meals to support templates and daily consumption tracking

-- Add new columns to meals table to support templates
ALTER TABLE meals ADD COLUMN is_template BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE meals ADD COLUMN is_one_time BOOLEAN NOT NULL DEFAULT false;

-- Update existing meals to be templates
UPDATE meals SET is_template = true, is_one_time = false WHERE is_template IS NULL;

-- Remove consumed and consumed_at from meals table as we'll track this separately
ALTER TABLE meals DROP COLUMN IF EXISTS consumed;
ALTER TABLE meals DROP COLUMN IF EXISTS consumed_at;

-- Create meal_consumptions table to track daily consumption
CREATE TABLE meal_consumptions (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    consumption_date DATE NOT NULL,
    consumed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_meal_consumptions_meal_id
        FOREIGN KEY (meal_id) REFERENCES meals (id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_consumptions_user_id
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    -- Ensure a meal can only be consumed once per day
    CONSTRAINT uq_meal_consumption_per_day UNIQUE (meal_id, user_id, consumption_date)
);

-- Create indexes for meal_consumptions
CREATE INDEX idx_meal_consumptions_meal_id ON meal_consumptions (meal_id);
CREATE INDEX idx_meal_consumptions_user_id ON meal_consumptions (user_id);
CREATE INDEX idx_meal_consumptions_date ON meal_consumptions (consumption_date);
CREATE INDEX idx_meal_consumptions_user_date ON meal_consumptions (user_id, consumption_date);

-- Add comments
COMMENT ON TABLE meal_consumptions IS 'Tracks daily consumption of meal templates';
COMMENT ON COLUMN meals.is_template IS 'Indicates if this meal is a reusable template';
COMMENT ON COLUMN meals.is_one_time IS 'Indicates if this meal is a one-time spontaneous meal';
