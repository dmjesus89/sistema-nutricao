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
CREATE INDEX idx_water_intake_user_date ON water_intake (user_id, intake_date);