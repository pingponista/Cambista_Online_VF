-- ====================================================
-- V3: CREATE PROCESSED EVENTS TABLE FOR IDEMPOTENCY
-- Microservicio: wallet-ledger-service
-- Deduplicación estricta y garantía At-Least-Once
-- ====================================================

CREATE TABLE IF NOT EXISTS tb_processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    consumer_group VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_processed_events_agg ON tb_processed_events(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_processed_events_type ON tb_processed_events(event_type);
