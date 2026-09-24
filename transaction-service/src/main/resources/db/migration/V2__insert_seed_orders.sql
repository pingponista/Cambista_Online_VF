-- ====================================================
-- V2: SEED TRANSACTION ORDERS FOR TESTING
-- ====================================================

INSERT INTO tb_operacion (
    nro_orden, tipo_operacion, moneda_origen, moneda_destino,
    monto_origen, monto_destino, tasa_final, puntos_canjeados,
    estado, correo_user, rol, fecha_expiracion, created_by
)
VALUES
(
    'TRX-987654321', 'COMPRA', 'USD', 'PEN',
    2500.00, 9422.50, 3.7690, 50,
    'PENDING_PAYMENT', 'demo@cambistaonline.pe', 'J',
    CURRENT_TIMESTAMP + INTERVAL '15 minutes', 'SEED_DATA'
),
(
    'TRX-123456789', 'VENTA', 'PEN', 'USD',
    1000.00, 265.25, 3.7700, 0,
    'COMPLETED', 'demo@cambistaonline.pe', 'J',
    CURRENT_TIMESTAMP - INTERVAL '1 hour', 'SEED_DATA'
)
ON CONFLICT DO NOTHING;
