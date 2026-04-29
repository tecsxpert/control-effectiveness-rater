CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    entity_name VARCHAR(100),
    entity_id BIGINT,
    performed_by VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_entity ON audit_log(entity_name);
CREATE INDEX idx_audit_performed_by ON audit_log(performed_by);