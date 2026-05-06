-- V3__performance_indexes.sql
-- Performance indexes to fix slow queries

-- Composite index for search and filter queries
CREATE INDEX IF NOT EXISTS idx_control_search ON control_effectiveness(control_name, category, status);

-- Index for date range queries (already exists in V1 as idx_control_created_at — skip duplicate)
-- CREATE INDEX IF NOT EXISTS idx_control_created_at ON control_effectiveness(created_at);

-- Index for score based queries
CREATE INDEX IF NOT EXISTS idx_control_score ON control_effectiveness(effectiveness_score);

-- Index for risk level queries (V1 created idx_control_risk_level; this is a separate composite)
CREATE INDEX IF NOT EXISTS idx_control_risk ON control_effectiveness(risk_level, effectiveness_score);

-- Composite index for audit log queries (entity_type is the correct column name)
CREATE INDEX IF NOT EXISTS idx_audit_entity_date ON audit_log(entity_type, created_at);
