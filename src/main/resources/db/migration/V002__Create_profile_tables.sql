-- V002__Create_profile_tables.sql
-- Migration for user profile-related tables

-- Create User Profiles Table
CREATE TABLE user_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    birth_date DATE,
    gender VARCHAR(10),
    height DECIMAL(5,2),
    current_weight DECIMAL(5,2),
    target_weight DECIMAL(5,2),
    target_date DATE,
    activity_level VARCHAR(20) NOT NULL DEFAULT 'SEDENTARY',
    goal VARCHAR(20) NOT NULL DEFAULT 'MAINTAIN_WEIGHT',
    basal_metabolic_rate DECIMAL(7,2),
    total_daily_energy_expenditure DECIMAL(7,2),
    daily_calories DECIMAL(7,2),
    daily_water_intake DECIMAL(6,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_user_profiles_user_id 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create indexes for user_profiles
CREATE INDEX idx_user_profiles_user_id ON user_profiles (user_id);
CREATE INDEX idx_user_profiles_gender ON user_profiles (gender);
CREATE INDEX idx_user_profiles_activity_level ON user_profiles (activity_level);

-- Create Weight History Table
CREATE TABLE weight_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    recorded_date DATE NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_weight_history_user_id 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_weight_history_user_date 
        UNIQUE (user_id, recorded_date)
);

-- Create indexes for weight_history
CREATE INDEX idx_weight_history_user_id ON weight_history (user_id);
CREATE INDEX idx_weight_history_recorded_date ON weight_history (recorded_date);
CREATE INDEX idx_weight_history_user_date ON weight_history (user_id, recorded_date);

-- Add check constraints for enum values and valid ranges
ALTER TABLE user_profiles 
    ADD CONSTRAINT chk_user_profiles_gender 
    CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'));

ALTER TABLE user_profiles 
    ADD CONSTRAINT chk_user_profiles_activity_level 
    CHECK (activity_level IN ('SEDENTARY', 'LIGHTLY_ACTIVE', 'MODERATELY_ACTIVE', 'VERY_ACTIVE', 'EXTREMELY_ACTIVE'));

ALTER TABLE user_profiles 
    ADD CONSTRAINT chk_user_profiles_goal 
    CHECK (goal IN ('LOSE_WEIGHT', 'MAINTAIN_WEIGHT', 'GAIN_WEIGHT'));

ALTER TABLE user_profiles 
    ADD CONSTRAINT chk_user_profiles_height_range 
    CHECK (height BETWEEN 100 AND 250);

ALTER TABLE user_profiles 
    ADD CONSTRAINT chk_user_profiles_current_weight_range 
    CHECK (current_weight BETWEEN 30 AND 300);

ALTER TABLE user_profiles 
    ADD CONSTRAINT chk_user_profiles_target_weight_range 
    CHECK (target_weight BETWEEN 30 AND 300);

ALTER TABLE weight_history 
    ADD CONSTRAINT chk_weight_history_weight_range 
    CHECK (weight BETWEEN 30 AND 300);