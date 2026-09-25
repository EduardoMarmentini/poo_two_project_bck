# Docker e banco de dados

Este guia descreve a inicializacao local do PostgreSQL e da API, a conta de conexao da aplicacao e os dados iniciais do sistema.

## 1. Pre-requisitos e inicializacao

Instale Docker Engine e Docker Compose v2. No terminal, entre na pasta `poo_two_project_frt`, onde esta o `docker-compose.yml`, e execute:

```sh
docker compose up --build -d
docker compose ps
```

O Compose inicia o PostgreSQL, aguarda o banco ficar pronto e entao inicia a API. A aplicacao fica disponivel em `http://localhost:8080`; o frontend e servido em `http://localhost:3000` e o Nginx em `https://techhub.local` (se os certificados e a entrada DNS local estiverem configurados).

Para acompanhar a inicializacao:

```sh
docker compose logs -f postgres backend
```

## 2. Banco e usuario de conexao

O servico `postgres` cria o banco `avaliacao` e a conta `poo_user` com as variaveis `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` do Compose. A API conecta ao host `postgres` na rede interna do Compose, nao a `localhost`, usando as variaveis `SPRING_DATASOURCE_*`.

No `docker-compose.yml` atual, tanto `POSTGRES_PASSWORD` quanto `SPRING_DATASOURCE_PASSWORD` estao definidos como `123456`. Portanto, o PostgreSQL aceita essa senha para `poo_user` e o Spring usa essa mesma senha para conectar. Se escolher outra senha, altere os dois valores para que continuem iguais.

Na imagem oficial do PostgreSQL, o usuario definido em `POSTGRES_USER` e superusuario. Isso simplifica o ambiente local e permite ao Hibernate criar/atualizar tabelas porque `SPRING_JPA_HIBERNATE_DDL_AUTO` esta como `update`; nao e uma configuracao recomendada para producao. Para producao, use uma conta de migracao proprietaria do schema e uma conta de runtime com apenas `CONNECT`, `USAGE` e permissao de leitura/escrita nas tabelas e sequencias necessarias, e desative o DDL automatico.

Se configurar o banco manualmente, crie a conta como proprietaria do banco, para que o Hibernate possa criar e atualizar os objetos do schema:

```sql
CREATE ROLE poo_user LOGIN PASSWORD '123456';
CREATE DATABASE avaliacao OWNER poo_user;
```

O valor de `PASSWORD` precisa ser exatamente o mesmo de `SPRING_DATASOURCE_PASSWORD` no servico `backend`. No Compose atual, use `123456`; se trocar por outra senha, use a nova senha tanto no `CREATE ROLE` quanto em `SPRING_DATASOURCE_PASSWORD` e `POSTGRES_PASSWORD`. O Compose nao lê a senha definida manualmente no SQL para configurar o Spring.

Conectado ao banco `avaliacao`, as permissoes minimas de schema para a configuracao atual sao:

```sql
GRANT CONNECT ON DATABASE avaliacao TO poo_user;
GRANT USAGE, CREATE ON SCHEMA public TO poo_user;
```

Execute os comandos de criacao com uma conta administrativa do PostgreSQL. Se o banco for criado com `OWNER poo_user`, a conta tambem sera proprietaria do banco. Ajuste `POSTGRES_PASSWORD` e `SPRING_DATASOURCE_PASSWORD` para o mesmo valor.

Se o volume do PostgreSQL ja tiver sido inicializado, mudar `POSTGRES_PASSWORD` no Compose nao altera a senha existente no banco. Nesse caso, altere-a no PostgreSQL com `ALTER ROLE poo_user WITH PASSWORD 'nova-senha';` e configure `SPRING_DATASOURCE_PASSWORD` com o mesmo valor antes de reiniciar a API.

## 3. Tabelas e dados criados automaticamente

O Compose monta `src/main/resources/sql` em `/docker-entrypoint-initdb.d`. Em um volume PostgreSQL novo, os scripts sao executados em ordem alfabetica:

- `01_create_auth_tables.sql`: cria `roles`, `users` e `user_roles`; insere as tres roles e os tres usuarios de demonstracao.
- `02_seed_demo.sql`: cria `fornecedores` e `mercadorias`; insere dados demonstrativos e ajusta as sequencias.

O `DataInitializer` da API tambem garante as tres roles e os usuarios padrao na inicializacao. Os inserts SQL e o inicializador sao idempotentes para esses nomes de usuario. As credenciais demonstrativas sao `admin`, `manager` e `user`, todas com senha `123456`; cada conta recebe somente uma role. Troque essas senhas antes de qualquer uso fora de desenvolvimento.

Os scripts da imagem oficial rodam **somente quando o diretorio de dados esta vazio**. Reiniciar containers preservando o volume nao reaplica os scripts. Para apagar o banco local e recria-lo do zero (isso remove todos os dados), rode da pasta do Compose:

```sh
docker compose down -v
docker compose up --build -d
```

## 4. Usuarios da aplicacao e roles

As roles validas sao `SYSTEM_ADMIN`, `SYSTEM_MANAGER` e `SYSTEM_USER`. As senhas devem ser armazenadas em BCrypt. O exemplo abaixo reutiliza o hash BCrypt da senha demonstrativa `123456` presente em `01_create_auth_tables.sql`; prefira criar usuarios pela aplicacao ou gerar um hash novo para outros ambientes.

```sql
INSERT INTO users (username, email, password, enabled) VALUES
    ('admin', 'admin@techhub.local', '$2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.', TRUE),
    ('manager', 'manager@techhub.local', '$2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.', TRUE),
    ('user', 'user@techhub.local', '$2b$10$BOtXqlXadoOiW50uYDnw9uxrWUBfGIAgZlqQ/J16OwlSiyL1jjgC.', TRUE)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u CROSS JOIN roles r
WHERE (u.username, r.name) IN (
    ('admin', 'SYSTEM_ADMIN'),
    ('manager', 'SYSTEM_MANAGER'),
    ('user', 'SYSTEM_USER')
)
ON CONFLICT DO NOTHING;
```

Confira que cada usuario tem uma unica role:

```sql
SELECT u.username, u.email, r.name AS role
FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON r.id = ur.role_id
ORDER BY u.username;
```

## 5. Exemplo minimo de fornecedor e mercadorias

O seed automatico ja adiciona varios registros. Use este exemplo apenas se quiser inserir registros adicionais manualmente; ele cria dois fornecedores e tres mercadorias associadas:

```sql
WITH fornecedor_a AS (
    INSERT INTO fornecedores (nome, contato, cidade, estado)
    VALUES ('Distribuidora Exemplo', '(11) 90000-0001', 'Sao Paulo', 'SP')
    RETURNING id
), fornecedor_b AS (
    INSERT INTO fornecedores (nome, contato, cidade, estado)
    VALUES ('Suprimentos Exemplo', '(41) 90000-0002', 'Curitiba', 'PR')
    RETURNING id
)
INSERT INTO mercadorias (nome, descricao, data_validade, quantidade, fornecedor_id)
SELECT 'Teclado USB', 'Teclado para escritorio', '2028-12-31', 12, id FROM fornecedor_a
UNION ALL
SELECT 'Mouse USB', 'Mouse optico', '2028-12-31', 20, id FROM fornecedor_a
UNION ALL
SELECT 'Cabo de rede', 'Cabo Cat6 de 2 metros', NULL, 35, id FROM fornecedor_b;
```

Confira os registros e o relacionamento:

```sql
SELECT m.nome AS mercadoria, m.quantidade, f.nome AS fornecedor
FROM mercadorias m
LEFT JOIN fornecedores f ON f.id = m.fornecedor_id
ORDER BY f.nome, m.nome;
```

## 6. Acesso ao PostgreSQL

Para abrir o `psql` no container:

```sh
docker compose exec postgres psql -U poo_user -d avaliacao
```

Para parar os servicos sem apagar o banco:

```sh
docker compose down
```