-- ====================================================
-- V2: SEED DATA FOR EXCHANGE RATE ENGINE
-- ====================================================

-- 1. Seed SBS Base Rates (USD and EUR against PEN)
INSERT INTO tb_tasa_base (moneda_origen, moneda_destino, valor_compra, valor_venta, fecha_efectiva, is_active, created_by)
VALUES 
('USD', 'PEN', 3.7565, 3.7650, CURRENT_TIMESTAMP, TRUE, 'SEED_DATA'),
('EUR', 'PEN', 4.0820, 4.1050, CURRENT_TIMESTAMP, TRUE, 'SEED_DATA')
ON CONFLICT DO NOTHING;

-- 2. Seed Customer Spreads by Level and Profile
INSERT INTO tb_spread_nivel (tipo_usuario, nivel_cliente, spread_compra, spread_venta, is_active, created_by)
VALUES 
('N', 'NORMAL', 0.0150, 0.0150, TRUE, 'SEED_DATA'),
('N', 'PREFERENTE', 0.0100, 0.0100, TRUE, 'SEED_DATA'),
('N', 'VIP', 0.0070, 0.0070, TRUE, 'SEED_DATA'),
('J', 'NORMAL', 0.0120, 0.0120, TRUE, 'SEED_DATA'),
('J', 'PREFERENTE', 0.0080, 0.0080, TRUE, 'SEED_DATA'),
('J', 'VIP', 0.0050, 0.0050, TRUE, 'SEED_DATA')
ON CONFLICT DO NOTHING;

-- 3. Seed Hourly Adjustments (Time Windows)
INSERT INTO tb_horario (tipo_usuario, hora_inicio, hora_fin, ajuste_compra, ajuste_venta, descripcion, is_active, created_by)
VALUES
('J', '19:00:00', '22:00:00', 0.0010, 0.0010, 'Ajuste Horario Nocturno Temprano Juridica', TRUE, 'SEED_DATA'),
('J', '22:00:00', '23:59:59', 0.0015, 0.0015, 'Ajuste Horario Nocturno Medio Juridica', TRUE, 'SEED_DATA'),
('J', '00:00:00', '03:00:00', 0.0020, 0.0020, 'Ajuste Horario Madrugada Juridica', TRUE, 'SEED_DATA'),
('J', '03:00:00', '06:00:00', 0.0030, 0.0030, 'Ajuste Horario Madrugada Profunda Juridica', TRUE, 'SEED_DATA'),
('N', '19:00:00', '22:00:00', 0.0012, 0.0012, 'Ajuste Horario Nocturno Natural', TRUE, 'SEED_DATA'),
('N', '22:00:00', '06:00:00', 0.0025, 0.0025, 'Ajuste Horario Madrugada Natural', TRUE, 'SEED_DATA')
ON CONFLICT DO NOTHING;

-- 4. Seed Seasonal Adjustments
INSERT INTO tb_estacionalidad (tipo_usuario, fecha_inicio, fecha_fin, ajuste_compra, ajuste_venta, descripcion, is_active, created_by)
VALUES
('J', '2026-01-01 00:00:00', '2026-12-31 23:59:59', -0.0010, -0.0010, 'Descuento Anual Promocional Empresa', TRUE, 'SEED_DATA'),
('N', '2026-01-01 00:00:00', '2026-12-31 23:59:59', -0.0005, -0.0005, 'Descuento Anual Promocional Persona Natural', TRUE, 'SEED_DATA')
ON CONFLICT DO NOTHING;

-- 5. Seed Points Rules Config
INSERT INTO tb_puntos_config (tipo_usuario, canje_minimo, puntos_por_bloque, mejora_por_bloque, is_active, created_by)
VALUES
('N', 100, 100, 0.0005, TRUE, 'SEED_DATA'),
('J', 100, 100, 0.0005, TRUE, 'SEED_DATA')
ON CONFLICT DO NOTHING;
