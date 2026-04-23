-- V1__init.sql
-- Core table for Control Effectiveness Rater

CREATE TABLE control_effectiveness (
    id BIGSERIAL PRIMARY KEY,
    control_name VARCHAR(255) NOT NULL,
    control_description TEXT,
    category VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING',
    effectiveness_score INTEGER CHECK (effectiveness_score BETWEEN 0 AND 100),
    risk_level VARCHAR(50),
    owner VARCHAR(255),
    ai_description TEXT,
    ai_recommendations TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for faster search
CREATE INDEX idx_control_status ON control_effectiveness(status);
CREATE INDEX idx_control_category ON control_effectiveness(category);
CREATE INDEX idx_control_owner ON control_effectiveness(owner);
CREATE INDEX idx_control_deleted ON control_effectiveness(is_deleted);