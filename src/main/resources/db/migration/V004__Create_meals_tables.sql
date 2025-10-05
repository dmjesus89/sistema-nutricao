-- V004__Create_meals_tables.sql
-- Migration for meals management tables

-- Create Meals Table
CREATE TABLE meals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    meal_time TIME NOT NULL,
    user_id BIGINT NOT NULL,
    consumed BOOLEAN NOT NULL DEFAULT false,
    consumed_at TIMESTAMP,
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

-- Add table comments
COMMENT ON TABLE meals IS 'Stores user meals with timing information';
COMMENT ON TABLE meal_foods IS 'Junction table linking meals to foods with quantities';