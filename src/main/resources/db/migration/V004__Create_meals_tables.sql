-- V004__Create_meals_tables.sql
-- Migration for meals management tables with template and consumption tracking

-- Create Meals Table (Templates)
CREATE TABLE meals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    meal_time TIME NOT NULL,
    user_id BIGINT NOT NULL,
    is_template BOOLEAN NOT NULL DEFAULT true,
    is_one_time BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_meals_user_id
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create indexes for meals
CREATE INDEX idx_meals_user_id ON meals (user_id);
CREATE INDEX idx_meals_user_meal_time ON meals (user_id, meal_time);
CREATE INDEX idx_meals_created_at ON meals (created_at);
CREATE INDEX idx_meals_name ON meals (name);
CREATE INDEX idx_meals_user_created_date ON meals (user_id, created_at);
CREATE INDEX idx_meals_is_template ON meals (is_template);
CREATE INDEX idx_meals_is_one_time ON meals (is_one_time);

-- Create Meal Foods Junction Table
CREATE TABLE meal_foods (
    id BIGSERIAL PRIMARY KEY,
    meal_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    quantity DECIMAL(8,2) NOT NULL,
    unit VARCHAR(20) DEFAULT 'g',
    CONSTRAINT fk_meal_foods_meal_id
        FOREIGN KEY (meal_id) REFERENCES meals (id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_foods_food_id
        FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE
);

-- Create indexes for meal_foods
CREATE INDEX idx_meal_foods_meal_id ON meal_foods (meal_id);
CREATE INDEX idx_meal_foods_food_id ON meal_foods (food_id);

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

-- Add table comments
COMMENT ON TABLE meals IS 'Stores user meal templates and one-time meals';
COMMENT ON TABLE meal_foods IS 'Junction table linking meals to foods with quantities';
COMMENT ON TABLE meal_consumptions IS 'Tracks daily consumption of meal templates';
COMMENT ON COLUMN meals.is_template IS 'Indicates if this meal is a reusable template';
COMMENT ON COLUMN meals.is_one_time IS 'Indicates if this meal is a one-time spontaneous meal';
