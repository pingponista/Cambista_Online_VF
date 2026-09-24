-- V2__insert_roles_and_initial_data.sql
INSERT INTO users (
    id, email, password, first_name, last_name, dni, company_name, ruc, legal_representative_name, role, status, mfa_enabled, auth_provider, created_at, updated_at
) VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'admin@cambistaonline.pe',
    '$2a$10$wE0v/V1FjCgO5fP3kR/6O.R7Y4aQ1J7gN7Z4aQ1J7gN7Z4aQ1J7gN', -- Hash BCrypt de "Password123*"
    'Administrador',
    'Sistema',
    '00000000',
    NULL,
    NULL,
    NULL,
    'ADMIN',
    'ACTIVE',
    FALSE,
    'LOCAL',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;
