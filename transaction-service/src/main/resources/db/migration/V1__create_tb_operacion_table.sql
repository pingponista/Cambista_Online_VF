-- ====================================================
-- V1: CREATE EXCHANGE TRANSACTION ORDERS TABLE (tb_operacion)
-- Microservicio: transaction-service
-- ====================================================

CREATE TABLE IF NOT EXISTS tb_operacion (
    id BIGSERIAL PRIMARY KEY,
    nro_orden VARCHAR(50) NOT NULL UNIQUE,
    tipo_operacion VARCHAR(20) NOT NULL, -- 'COMPRA' or 'VENTA'
    moneda_origen VARCHAR(10) NOT NULL, -- 'USD', 'PEN', 'EUR'
    moneda_destino VARCHAR(10) NOT NULL,
    monto_origen DECIMAL(12,2) NOT NULL,
    monto_destino DECIMAL(12,2) NOT NULL,
    tasa_final DECIMAL(10,4) NOT NULL,
    puntos_canjeados INT NOT NULL DEFAULT 0,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    correo_user VARCHAR(150) NOT NULL,
    rol VARCHAR(5) NOT NULL,
    fecha_expiracion TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- Performance Indexes
CREATE INDEX IF NOT EXISTS idx_operacion_correo ON tb_operacion(correo_user, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_operacion_nro ON tb_operacion(nro_orden);
CREATE INDEX IF NOT EXISTS idx_operacion_estado ON tb_operacion(estado);
