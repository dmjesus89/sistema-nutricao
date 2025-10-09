-- V010__Create_config_tables.sql
-- Migration for configuration tables (activity levels and goals)

-- Create Activity Levels Table
CREATE TABLE activity_levels (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    multiplier DECIMAL(4,3) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for activity_levels
CREATE INDEX idx_activity_levels_code ON activity_levels (code);
CREATE INDEX idx_activity_levels_active ON activity_levels (active);
CREATE INDEX idx_activity_levels_order ON activity_levels (display_order);

-- Create Goals Table
CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    calorie_adjustment_type VARCHAR(20) NOT NULL,
    calorie_adjustment_value DECIMAL(7,2),
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for goals
CREATE INDEX idx_goals_code ON goals (code);
CREATE INDEX idx_goals_active ON goals (active);
CREATE INDEX idx_goals_order ON goals (display_order);

-- Add check constraint for calorie_adjustment_type
ALTER TABLE goals
    ADD CONSTRAINT chk_goals_adjustment_type
    CHECK (calorie_adjustment_type IN ('PERCENTAGE', 'FIXED', 'NONE'));

-- Insert default activity levels
INSERT INTO activity_levels (code, display_name, description, multiplier, display_order) VALUES
('SEDENTARY', 'Sedentary', 'Seated work, minimal movement, very still routine.', 1.200, 1),
('LIGHTLY_ACTIVE', 'Lightly Active', 'Seated work but moves throughout the day (walks a lot, shops, takes care of home, uses public transport, etc.).', 1.375, 2),
('MODERATELY_ACTIVE', 'Moderately Active', 'Routine with frequent movement + some recreational physical activities (e.g., walks a lot daily + takes long walks, but no structured training).', 1.550, 3),
('VERY_ACTIVE', 'Very Active', 'Physically demanding work (bricklayer, waiter walking 10h, bike delivery, moving helper, etc.).', 1.725, 4),
('EXTREMELY_ACTIVE', 'Extremely Active', 'Heavy load rural workers, athletes, military, people who spend energy intensively daily.', 1.900, 5);

-- Insert default goals
INSERT INTO goals (code, display_name, description, calorie_adjustment_type, calorie_adjustment_value, display_order) VALUES
('LOSE_WEIGHT', 'Lose Fat/Maintain Lean Muscle', 'Moderate caloric deficit for fat loss while preserving lean muscle.', 'PERCENTAGE', -15.00, 1),
('MAINTAIN_WEIGHT', 'Maintain Weight', 'Maintaining current weight with caloric balance.', 'NONE', 0.00, 2),
('GAIN_WEIGHT', 'Gain Weight', 'Caloric surplus for muscle mass gain.', 'PERCENTAGE', 10.00, 3);
