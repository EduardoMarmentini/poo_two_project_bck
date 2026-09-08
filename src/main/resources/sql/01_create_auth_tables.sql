-- ============================================
-- Script SQL - Sistema de Autenticação OAuth2 + OIDC
-- Roles: SYSTEM_ADMIN, SYSTEM_USER, SYSTEM_MANAGER
-- ============================================

-- Tabela de roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de junção usuários <-> roles (N:M)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_roles_user ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role_id);

-- Trigger para updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- Seeds - Roles
-- ============================================
INSERT INTO roles (name, description) VALUES
    ('SYSTEM_ADMIN', 'Administrador do sistema - acesso total'),
    ('SYSTEM_MANAGER', 'Gerente - pode cadastrar produtos/fornecedores, deletar e movimentar estoque'),
    ('SYSTEM_USER', 'Usuário padrão - pode acessar telas e registrar entradas/saídas')
ON CONFLICT (name) DO NOTHING;

-- ============================================
-- Seeds - Usuários (senha: 123456 BCrypt)
-- Hash BCrypt com strength 10 para senha "123456":
-- $2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.
-- Em produção gere com: BCryptPasswordEncoder.encode("123456")
-- ============================================
INSERT INTO users (username, email, password, enabled) VALUES
    ('admin', 'admin@techhub.local', '$2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.', TRUE),
    ('manager', 'manager@techhub.local', '$2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.', TRUE),
    ('user', 'user@techhub.local', '$2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Vincula roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'SYSTEM_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'manager' AND r.name = 'SYSTEM_MANAGER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'user' AND r.name = 'SYSTEM_USER'
ON CONFLICT DO NOTHING;

-- ============================================
-- Verificação
-- ============================================
-- SELECT u.username, u.email, string_agg(r.name, ', ') as roles
-- FROM users u JOIN user_roles ur ON u.id = ur.user_id JOIN roles r ON r.id = ur.role_id
-- GROUP BY u.id;
