-- ====================================================
-- V1: CREATE WALLET & DOUBLE-ENTRY LEDGER TABLES
-- Microservicio: wallet-ledger-service
-- ====================================================

CREATE TABLE IF NOT EXISTS tb_wallet_account (
    id UUID PRIMARY KEY,
    user_email VARCHAR(150) NOT NULL,
    currency VARCHAR(10) NOT NULL, -- 'PEN', 'USD', 'EUR'
    available_balance DECIMAL(14,4) NOT NULL DEFAULT 0.0000,
    locked_balance DECIMAL(14,4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_wallet_user_currency UNIQUE (user_email, currency)
);

CREATE TABLE IF NOT EXISTS tb_ledger_entry (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(150) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    direction VARCHAR(10) NOT NULL, -- 'DEBIT', 'CREDIT'
    amount DECIMAL(14,4) NOT NULL,
    movement_type VARCHAR(30) NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'EXCHANGE_DEBIT', 'EXCHANGE_CREDIT', 'LOCK', 'RELEASE'
    reference_id VARCHAR(100),
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_loyalty_points (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(150) NOT NULL UNIQUE,
    saldo_puntos INT NOT NULL DEFAULT 0,
    puntos_acumulados INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_wallet_account_user ON tb_wallet_account(user_email);
CREATE INDEX IF NOT EXISTS idx_ledger_entry_user ON tb_ledger_entry(user_email, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ledger_entry_ref ON tb_ledger_entry(reference_id);
CREATE INDEX IF NOT EXISTS idx_loyalty_points_email ON tb_loyalty_points(user_email);
