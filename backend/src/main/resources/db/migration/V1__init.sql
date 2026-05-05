-- V1__init.sql — Core table for Control Effectiveness Rater

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'USER',
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE control_effectiveness (
    id                  BIGSERIAL PRIMARY KEY,
    control_name        VARCHAR(255) NOT NULL,
    control_description TEXT,
    category            VARCHAR(100),
    risk_level          VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    effectiveness_score INTEGER      CHECK (effectiveness_score >= 0 AND effectiveness_score <= 100),
    status              VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    assessor            VARCHAR(100),
    department          VARCHAR(100),
    review_date         DATE,
    ai_description      TEXT,
    ai_recommendations  TEXT,
    ai_report           TEXT,
    is_fallback         BOOLEAN      NOT NULL DEFAULT FALSE,
    is_deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          BIGINT       REFERENCES users(id),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_control_status ON control_effectiveness(status);
CREATE INDEX idx_control_category ON control_effectiveness(category);
CREATE INDEX idx_control_risk_level ON control_effectiveness(risk_level);
CREATE INDEX idx_control_created_at ON control_effectiveness(created_at);
CREATE INDEX idx_control_is_deleted ON control_effectiveness(is_deleted);
CREATE INDEX idx_control_department ON control_effectiveness(department);
