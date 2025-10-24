-- V001__Create_auth_tables.sql
-- Migration for authentication-related tables

-- Create Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    email_confirmed BOOLEAN NOT NULL DEFAULT false,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
    enabled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for users table
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);

-- Create Email Confirmation Tokens Table
CREATE TABLE email_confirmation_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_confirmation_tokens_user_id 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create indexes for email_confirmation_tokens
CREATE INDEX idx_email_confirmation_tokens_token ON email_confirmation_tokens (token);
CREATE INDEX idx_email_confirmation_tokens_user_id ON email_confirmation_tokens (user_id);

-- Create Password Reset Tokens Table
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_tokens_user_id 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create indexes for password_reset_tokens
CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens (token);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);

-- Create Refresh Tokens Table
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user_id 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create indexes for refresh_tokens
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens (revoked);-- V002__Create_profile_tables.sql
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
    CHECK (weight BETWEEN 30 AND 300);-- V003__Create_foods_tables.sql
-- Migration for foods and nutrition-related tables

-- Create Foods Table
CREATE TABLE foods
(
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(200)  NOT NULL,
    description            VARCHAR(1000),
    brand                  VARCHAR(100),
    category               VARCHAR(50)   NOT NULL,
    barcode                VARCHAR(50),
    calories_per_100g      DECIMAL(8, 2) NOT NULL,
    carbs_per_100g         DECIMAL(6, 2) NOT NULL,
    protein_per_100g       DECIMAL(6, 2) NOT NULL,
    fat_per_100g           DECIMAL(6, 2) NOT NULL,
    fiber_per_100g         DECIMAL(6, 2),
    sugar_per_100g         DECIMAL(6, 2),
    sodium_per_100g        DECIMAL(8, 2),
    saturated_fat_per_100g DECIMAL(6, 2),
    serving_size           DECIMAL(6, 2),
    serving_description    VARCHAR(100),
    serving_unit           VARCHAR(20),
    quantity_equivalence   VARCHAR(100),
    source                 VARCHAR(100),
    verified               BOOLEAN       NOT NULL DEFAULT false,
    active                 BOOLEAN       NOT NULL DEFAULT true,
    created_by_user_id     BIGINT,
    created_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    CONSTRAINT fk_foods_created_by FOREIGN KEY (created_by_user_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT uk_foods_barcode UNIQUE (barcode),
    CONSTRAINT uk_foods_name UNIQUE (name)
);

-- Create indexes for foods
CREATE INDEX idx_foods_name ON foods (name);
CREATE INDEX idx_foods_category ON foods (category);
CREATE INDEX idx_foods_brand ON foods (brand);
CREATE INDEX idx_foods_verified_active ON foods (verified, active);
CREATE INDEX idx_foods_barcode ON foods (barcode);
CREATE INDEX idx_foods_serving_unit ON foods (serving_unit);

-- Create Supplements Table
CREATE TABLE supplements
(
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(200)  NOT NULL,
    description            VARCHAR(1000),
    brand                  VARCHAR(100),
    category               VARCHAR(50)   NOT NULL,
    form                   VARCHAR(20)   NOT NULL,
    serving_size           DECIMAL(8, 2) NOT NULL,
    serving_unit           VARCHAR(20)   NOT NULL,
    servings_per_container DECIMAL(6, 0),
    calories_per_serving   DECIMAL(6, 2),
    carbs_per_serving      DECIMAL(6, 2),
    protein_per_serving    DECIMAL(6, 2),
    fat_per_serving        DECIMAL(6, 2),
    main_ingredient        VARCHAR(200),
    ingredient_amount      DECIMAL(10, 2),
    ingredient_unit        VARCHAR(20),
    recommended_dosage     VARCHAR(500),
    usage_instructions     VARCHAR(1000),
    warnings               VARCHAR(1000),
    regulatory_info        VARCHAR(500),
    verified               BOOLEAN       NOT NULL DEFAULT false,
    active                 BOOLEAN       NOT NULL DEFAULT true,
    created_by_user_id     BIGINT,
    created_at             TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP,
    CONSTRAINT fk_supplements_created_by
        FOREIGN KEY (created_by_user_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Create indexes for supplements
CREATE INDEX idx_supplements_name ON supplements (name);
CREATE INDEX idx_supplements_category ON supplements (category);
CREATE INDEX idx_supplements_brand ON supplements (brand);
CREATE INDEX idx_supplements_verified_active ON supplements (verified, active);

-- Create User Food Preferences Table
CREATE TABLE user_food_preferences
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT      NOT NULL,
    food_id         BIGINT      NOT NULL,
    preference_type VARCHAR(20) NOT NULL,
    notes           VARCHAR(500),
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT fk_user_food_preferences_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_food_preferences_food
        FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_food_preferences
        UNIQUE (user_id, food_id)
);

-- Create index for user food preferences
CREATE INDEX idx_user_food_preferences_user_type ON user_food_preferences (user_id, preference_type);

-- Create User Supplements Table (frequency-based tracking)
CREATE TABLE user_supplements
(
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    supplement_id           BIGINT       NOT NULL,
    frequency               VARCHAR(30)  NOT NULL DEFAULT 'DAILY',
    notes                   VARCHAR(500),
    dosage_time             TIME,
    days_of_week            VARCHAR(100),
    email_reminder_enabled  BOOLEAN      NOT NULL DEFAULT FALSE,
    last_taken_at           TIMESTAMP,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    CONSTRAINT fk_user_supplements_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_supplements_supplement
        FOREIGN KEY (supplement_id) REFERENCES supplements (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_supplements
        UNIQUE (user_id, supplement_id),
    CONSTRAINT chk_user_supplements_frequency
        CHECK (frequency IN ('DAILY', 'WEEKLY', 'TWICE_WEEKLY', 'THREE_TIMES_WEEKLY', 'MONTHLY'))
);

-- Create indexes for user supplements
CREATE INDEX idx_user_supplements_user ON user_supplements (user_id);
CREATE INDEX idx_user_supplements_frequency ON user_supplements (frequency);
CREATE INDEX idx_user_supplements_reminder ON user_supplements (email_reminder_enabled, dosage_time) WHERE email_reminder_enabled = true;
CREATE INDEX idx_user_supplements_last_taken ON user_supplements (user_id, last_taken_at);

-- Create User Dietary Restrictions Table
CREATE TABLE user_dietary_restrictions
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    restriction_type VARCHAR(30) NOT NULL,
    severity         VARCHAR(20) NOT NULL DEFAULT 'MODERATE',
    notes            VARCHAR(500),
    active           BOOLEAN     NOT NULL DEFAULT true,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP,
    CONSTRAINT fk_user_dietary_restrictions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create index for user dietary restrictions
CREATE INDEX idx_user_dietary_restrictions_user_active ON user_dietary_restrictions (user_id, active);-- V004__Create_meals_tables.sql
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
-- V005__Create_water_intake_table.sql
-- Migration for water intake tracking

-- Create Water Intake Table
CREATE TABLE water_intake (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount_ml DECIMAL(8,2) NOT NULL,
    intake_date DATE NOT NULL,
    intake_time TIMESTAMP NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_water_intake_user 
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create index for water intake
CREATE INDEX idx_water_intake_user_date ON water_intake (user_id, intake_date);-- V006__Create_calorie_tracking_tables.sql
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
    );-- V010__Create_config_tables.sql
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
-- Add email notification tracking fields to users table
-- This allows us to track which welcome emails have been sent

ALTER TABLE users ADD COLUMN IF NOT EXISTS welcome_email_sent BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS welcome_email_sent_at TIMESTAMP;

-- Create index for querying users who haven't received welcome email
CREATE INDEX IF NOT EXISTS idx_users_welcome_email_pending
    ON users (welcome_email_sent, enabled, created_at)
    WHERE welcome_email_sent = false AND enabled = true;

COMMENT ON COLUMN users.welcome_email_sent IS 'Indicates whether the welcome email has been sent after account confirmation';
COMMENT ON COLUMN users.welcome_email_sent_at IS 'Timestamp when the welcome email was sent';
