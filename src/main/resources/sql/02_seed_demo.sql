-- ============================================
-- 02_seed_demo.sql - Dados demo para TechHub
-- Fornecedores + Mercadorias (popula banco Docker)
-- Executado automaticamente via /docker-entrypoint-initdb.d em postgres:16
-- ============================================

-- Garante que tabelas existem (caso 01 não tenha rodado por volume já existente)
CREATE TABLE IF NOT EXISTS fornecedores (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    contato VARCHAR(255),
    rua VARCHAR(255),
    numero VARCHAR(255),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(255),
    cep VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mercadorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    data_validade DATE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantidade INTEGER NOT NULL CHECK (quantidade >= 0),
    fornecedor_id BIGINT REFERENCES fornecedores(id) ON DELETE SET NULL
);

-- Fornecedores demo
INSERT INTO fornecedores (id, nome, contato, rua, numero, complemento, bairro, cidade, estado, cep, created_at) VALUES
 (1, 'Tech Distribuidora Atualizada', '(11) 99999-9999', 'Av. Tecnologia', '1500', 'Galpão 2', 'Distrito Industrial', 'São Paulo', 'SP', '01000-000', NOW()),
 (3, 'InovaTech Suprimentos', '(41) 3355-7788', 'Av. Inovação', '890', 'Sala 12', 'Tecnológico', 'Curitiba', 'PR', '80000-000', NOW()),
 (4, 'Mega Components Brasil', '(31) 3222-5566', 'Rua Industrial', '300', 'Bloco B', 'Industrial', 'Belo Horizonte', 'MG', '30000-000', NOW()),
 (5, 'Digital Parts Distribuição', '(51) 3777-8899', 'Av. Digital', '1500', NULL, 'Comercial', 'Porto Alegre', 'RS', '90000-000', NOW())
ON CONFLICT (id) DO NOTHING;

-- Mercadorias demo - vinculadas aos fornecedores acima
INSERT INTO mercadorias (id, nome, descricao, data_validade, data_cadastro, quantidade, fornecedor_id) VALUES
 (1, 'Notebook Lenovo i5', 'Notebook 8GB RAM SSD 256GB', '2028-12-31', NOW(), 14, 1),
 (2, 'Mouse Logitech Wireless', 'Mouse sem fio USB', '2028-12-31', NOW(), 100, 1),
 (3, 'Teclado Mecânico RGB', 'Teclado gamer ABNT2', '2028-12-31', NOW(), 20, 1),
 (4, 'Monitor 24 Polegadas', 'Full HD HDMI', '2028-12-31', NOW(), 10, 1),
 (5, 'Headset Gamer', 'Headset com microfone', NULL, NOW(), 1, 1),
 (6, 'Webcam Full HD', 'Câmera para streaming', '2028-12-31', NOW(), 25, 1),
 (7, 'SSD 480GB', 'Armazenamento interno', '2028-12-31', NOW(), 30, 1),
 (8, 'Memória RAM 8GB DDR4', 'Memória para desktop', '2028-12-31', NOW(), 18, 1),
 (10, 'Placa de Vídeo GTX 1660', 'GPU dedicada', '2028-12-31', NOW(), 1000, 1),
 (21, 'Roteador Wi-Fi', 'Dual Band', '2028-12-31', NOW(), 25, 3),
 (22, 'Switch 8 Portas', 'Rede cabeada', '2028-12-31', NOW(), 15, 3),
 (23, 'Access Point', 'Expansor de sinal', '2028-12-31', NOW(), 10, 3),
 (24, 'Cabo de Rede Cat6', 'Alta velocidade', '2028-12-31', NOW(), 100, 3),
 (27, 'No-break 1200VA', 'Proteção energia', '2028-12-31', NOW(), 12, 3),
 (29, 'Conector RJ45', 'Conector de rede', '2028-12-31', NOW(), 200, 3),
 (30, 'Placa de Rede', 'PCI Express', '2028-12-31', NOW(), 20, 3),
 (31, 'Processador Intel i7', '8 núcleos', '2028-12-31', NOW(), 7, 4),
 (32, 'Placa Mãe ATX', 'Compatível Intel', '2028-12-31', NOW(), 10, 4),
 (35, 'HD 1TB', 'Armazenamento', '2028-12-31', NOW(), 20, 4),
 (36, 'SSD NVMe 1TB', 'Alta performance', '2028-12-31', NOW(), 6, 4),
 (37, 'Placa de Vídeo RTX 3060', 'GPU avançada', '2028-12-31', NOW(), 0, 4),
 (41, 'Impressora Multifuncional', 'Scanner + impressão', '2028-12-31', NOW(), 8, 5),
 (42, 'Cartucho de Tinta', 'Preto', '2028-12-31', NOW(), 40, 5),
 (44, 'Projetor', 'Full HD', '2028-12-31', NOW(), 3, 5),
 (50, 'Nobreak 600VA', 'Energia backup', '2028-12-31', NOW(), 0, 5),
 (52, 'Dock Station USB-C', 'Expansor de portas', '2028-12-31', NOW(), 14, 1),
 (65, 'Organizador de Cabos', 'Velcro', '2028-12-31', NOW(), 100, 3)
ON CONFLICT (id) DO NOTHING;

-- Ajusta sequences para não conflitar com próximos inserts
SELECT setval('fornecedores_id_seq', (SELECT COALESCE(MAX(id),1) FROM fornecedores), true);
SELECT setval('mercadorias_id_seq', (SELECT COALESCE(MAX(id),1) FROM mercadorias), true);
