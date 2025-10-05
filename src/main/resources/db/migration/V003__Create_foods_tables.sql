-- V003__Create_foods_tables.sql
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

-- Create User Supplement Preferences Table
CREATE TABLE user_supplement_preferences
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT      NOT NULL,
    supplement_id   BIGINT      NOT NULL,
    preference_type VARCHAR(20) NOT NULL,
    notes           VARCHAR(500),
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    CONSTRAINT fk_user_supplement_preferences_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_supplement_preferences_supplement
        FOREIGN KEY (supplement_id) REFERENCES supplements (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_supplement_preferences
        UNIQUE (user_id, supplement_id)
);

-- Create index for user supplement preferences
CREATE INDEX idx_user_supplement_preferences_user_type ON user_supplement_preferences (user_id, preference_type);

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
CREATE INDEX idx_user_dietary_restrictions_user_active ON user_dietary_restrictions (user_id, active);