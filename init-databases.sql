-- ==========================================================
-- SCRIPT DE INICIALIZACIÓN DE BASES DE DATOS INDEPENDIENTES
-- Principio de Arquitectura: Database per Service
-- ==========================================================

CREATE DATABASE cambista_auth_db;
CREATE DATABASE cambista_rate_db;
CREATE DATABASE cambista_wallet_db;
CREATE DATABASE cambista_transaction_db;

GRANT ALL PRIVILEGES ON DATABASE cambista_auth_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE cambista_rate_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE cambista_wallet_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE cambista_transaction_db TO postgres;
