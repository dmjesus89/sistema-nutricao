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
('SEDENTARY', 'Sedentário', 'Trabalha sentado, deslocamento mínimo, rotina bem parada.', 1.200, 1),
('LIGHTLY_ACTIVE', 'Levemente ativo', 'Trabalha sentado mas se movimenta no dia (anda bastante, faz compras, cuida da casa, pega transporte público etc.).', 1.375, 2),
('MODERATELY_ACTIVE', 'Moderadamente ativo', 'Rotina com movimento frequente + algumas atividades físicas recreativas (ex: anda muito todo dia + faz caminhadas longas, mas sem treino estruturado).', 1.550, 3),
('VERY_ACTIVE', 'Muito ativo', 'Trabalho fisicamente exigente (pedreiro, garçom andando 10h, entregador de bike, ajudante de mudanças, etc.).', 1.725, 4),
('EXTREMELY_ACTIVE', 'Extremamente ativo', 'Trabalhadores rurais de carga pesada, atletas, militares, pessoas que gastam energia de forma intensa diária.', 1.900, 5);

-- Insert default goals
INSERT INTO goals (code, display_name, description, calorie_adjustment_type, calorie_adjustment_value, display_order) VALUES
('LOSE_WEIGHT', 'Perder Gordura/Manter Massa Magra', 'Déficit calórico moderado para perda de gordura preservando massa magra.', 'PERCENTAGE', -15.00, 1),
('MAINTAIN_WEIGHT', 'Manter peso', 'Manutenção do peso atual com equilíbrio calórico.', 'NONE', 0.00, 2),
('GAIN_WEIGHT', 'Ganhar peso', 'Superávit calórico para ganho de massa muscular.', 'PERCENTAGE', 10.00, 3);
