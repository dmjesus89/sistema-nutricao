-- V006__Create_calorie_tracking_tables.sql
-- Migration for calorie tracking functionality

-- Create Calorie Entries Table
CREATE TABLE calorie_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    calories DECIMAL(8,2) NOT NULL,
    carbs DECIMAL(6,2),
    protein DECIMAL(6,2),
    fat DECIMAL(6,2),
    food_id BIGINT,
    meal_id BIGINT,
    quantity_grams DECIMAL(8,2),
    description VARCHAR(500),
    notes VARCHAR(1000),
    consumed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_calorie_entries_user 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_calorie_entries_food 
        FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE SET NULL,
    CONSTRAINT fk_calorie_entries_meal 
        FOREIGN KEY (meal_id) REFERENCES meals (id) ON DELETE SET NULL
);

-- Create indexes for calorie_entries
CREATE INDEX idx_calorie_entries_user_date ON calorie_entries (user_id, date);
CREATE INDEX idx_calorie_entries_user_consumed_at ON calorie_entries (user_id, consumed_at);
CREATE INDEX idx_calorie_entries_entry_type ON calorie_entries (entry_type);
CREATE INDEX idx_calorie_entries_food ON calorie_entries (food_id);
CREATE INDEX idx_calorie_entries_meal ON calorie_entries (meal_id);

-- Add check constraints for valid enum values and ranges
ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_entry_type 
    CHECK (entry_type IN ('MANUAL', 'FOOD', 'MEAL'));

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_calories_positive 
    CHECK (calories > 0);

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_carbs_non_negative 
    CHECK (carbs IS NULL OR carbs >= 0);

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_protein_non_negative 
    CHECK (protein IS NULL OR protein >= 0);

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_fat_non_negative 
    CHECK (fat IS NULL OR fat >= 0);

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_quantity_positive 
    CHECK (quantity_grams IS NULL OR quantity_grams > 0);

-- Business logic constraints
ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_food_logic 
    CHECK (
        (entry_type = 'FOOD' AND food_id IS NOT NULL AND quantity_grams IS NOT NULL) OR 
        (entry_type != 'FOOD')
    );

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_meal_logic 
    CHECK (
        (entry_type = 'MEAL' AND meal_id IS NOT NULL) OR 
        (entry_type != 'MEAL')
    );

ALTER TABLE calorie_entries 
    ADD CONSTRAINT chk_calorie_entries_manual_logic 
    CHECK (
        (entry_type = 'MANUAL' AND description IS NOT NULL AND description != '') OR 
        (entry_type != 'MANUAL')
    );