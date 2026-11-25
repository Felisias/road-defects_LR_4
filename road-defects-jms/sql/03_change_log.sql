CREATE TABLE IF NOT EXISTS change_log (
    id          BIGSERIAL   PRIMARY KEY,
    changed_at  TIMESTAMP   NOT NULL,
    change_type VARCHAR(10) NOT NULL CHECK (change_type IN ('INSERT', 'UPDATE', 'DELETE')),
    entity      VARCHAR(50) NOT NULL,
    entity_id   VARCHAR(50) NOT NULL,
    data        TEXT
);
