-- ====================================================
-- V1: CREATE EXCHANGE RATE ENGINE PARAMETERIZED TABLES
-- Microservicio: exchange-rate-service
-- ====================================================

-- 1. Table: Base Exchange Rates (SBS Rates)
CREATE TABLE IF NOT EXISTS tb_tasa_base (
    id BIGSERIAL PRIMARY KEY,
    moneda_origen VARCHAR(10) NOT NULL DEFAULT 'USD',
    moneda_destino VARCHAR(10) NOT NULL DEFAULT 'PEN',
    valor_compra DECIMAL(10,4) NOT NULL,
    valor_venta DECIMAL(10,4) NOT NULL,
    fecha_efectiva TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- 2. Table: Spreads by Profile ('N'/'J') and Customer Level ('NORMAL', 'PREFERENTE', 'VIP')
CREATE TABLE IF NOT EXISTS tb_spread_nivel (
    id BIGSERIAL PRIMARY KEY,
    tipo_usuario VARCHAR(5) NOT NULL, -- 'N' (Natural) or 'J' (Juridica)
    nivel_cliente VARCHAR(20) NOT NULL, -- 'NORMAL', 'PREFERENTE', 'VIP'
    spread_compra DECIMAL(10,4) NOT NULL,
    spread_venta DECIMAL(10,4) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    CONSTRAINT uk_spread_tipo_nivel UNIQUE (tipo_usuario, nivel_cliente)
);

-- 3. Table: Time Window Adjustments (Hourly Rules)
CREATE TABLE IF NOT EXISTS tb_horario (
    id BIGSERIAL PRIMARY KEY,
    tipo_usuario VARCHAR(5) NOT NULL, -- 'N' or 'J'
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    ajuste_compra DECIMAL(10,4) NOT NULL,
    ajuste_venta DECIMAL(10,4) NOT NULL,
    descripcion VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- 4. Table: Seasonal Date Range Adjustments
CREATE TABLE IF NOT EXISTS tb_estacionalidad (
    id BIGSERIAL PRIMARY KEY,
    tipo_usuario VARCHAR(5) NOT NULL, -- 'N' or 'J'
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    ajuste_compra DECIMAL(10,4) NOT NULL,
    ajuste_venta DECIMAL(10,4) NOT NULL,
    descripcion VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- 5. Table: Loyalty Points Calculation Rules Configuration
CREATE TABLE IF NOT EXISTS tb_puntos_config (
    id BIGSERIAL PRIMARY KEY,
    tipo_usuario VARCHAR(5) NOT NULL UNIQUE, -- 'N' or 'J'
    canje_minimo INT NOT NULL DEFAULT 100,
    puntos_por_bloque INT NOT NULL DEFAULT 100,
    mejora_por_bloque DECIMAL(10,4) NOT NULL DEFAULT 0.0005,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- 6. Table: User Loyalty Points (Cache / Local Engine View)
CREATE TABLE IF NOT EXISTS tb_usuario_puntos (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(150) NOT NULL UNIQUE,
    saldo_puntos INT NOT NULL DEFAULT 0,
    puntos_acumulados INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

-- Indexes for query performance
CREATE INDEX IF NOT EXISTS idx_tasa_base_active ON tb_tasa_base(is_active, fecha_efectiva DESC);
CREATE INDEX IF NOT EXISTS idx_spread_nivel_tipo ON tb_spread_nivel(tipo_usuario, nivel_cliente, is_active);
CREATE INDEX IF NOT EXISTS idx_horario_tipo ON tb_horario(tipo_usuario, is_active);
CREATE INDEX IF NOT EXISTS idx_estacionalidad_rango ON tb_estacionalidad(fecha_inicio, fecha_fin, is_active);
CREATE INDEX IF NOT EXISTS idx_usuario_puntos_email ON tb_usuario_puntos(user_email);
