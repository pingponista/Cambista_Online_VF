-- ====================================================
-- V2: SEED PLATFORM TREASURY & DEMO WALLETS
-- ====================================================

-- Demo User Wallets (PEN, USD, EUR)
INSERT INTO tb_wallet_account (id, user_email, currency, available_balance, locked_balance, status)
VALUES 
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380001', 'demo@cambistaonline.pe', 'PEN', 15000.0000, 0.0000, 'ACTIVE'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380002', 'demo@cambistaonline.pe', 'USD', 5000.0000, 0.0000, 'ACTIVE'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 'demo@cambistaonline.pe', 'EUR', 2000.0000, 0.0000, 'ACTIVE'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380004', 'admin@cambistaonline.pe', 'PEN', 100000.0000, 0.0000, 'ACTIVE'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380005', 'admin@cambistaonline.pe', 'USD', 50000.0000, 0.0000, 'ACTIVE'),
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380006', 'admin@cambistaonline.pe', 'EUR', 30000.0000, 0.0000, 'ACTIVE')
ON CONFLICT (user_email, currency) DO NOTHING;

-- Loyalty Points Initial Seed
INSERT INTO tb_loyalty_points (user_email, saldo_puntos, puntos_acumulados)
VALUES 
('demo@cambistaonline.pe', 320, 1500),
('admin@cambistaonline.pe', 1000, 5000)
ON CONFLICT (user_email) DO NOTHING;
