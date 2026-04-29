-- V3__performance_indexes.sql
-- Performance indexes to fix slow queries

-- Composite index for search and filter queries
CREATE INDEX idx_control_search ON control_effectiveness(control_name, category, status);

-- Index for date range queries
CREATE INDEX idx_control_created_at ON control_effectiveness(created_at);

-- Index for score based queries
CREATE INDEX idx_control_score ON control_effectiveness(effectiveness_score);

-- Index for risk level queries
CREATE INDEX idx_control_risk ON control_effectiveness(risk_level);

-- Composite index for audit log queries
CREATE INDEX idx_audit_entity_date ON audit_log(entity_name, created_at);