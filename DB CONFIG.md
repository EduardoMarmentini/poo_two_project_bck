# 🗄️ Configuração do Banco de Dados - Projeto POO II

Este guia descreve como configurar o banco de dados PostgreSQL para o projeto **poo_two_project_db**.

---

## 📌 Pré-requisitos

* PostgreSQL instalado
* Acesso ao terminal
* Usuário `postgres` configurado

---

## 🚀 1. Acessar o PostgreSQL

No terminal:

```bash
psql -U postgres
```

---

## 🧱 2. Criar o banco de dados

```sql
CREATE DATABASE avaliacao;
```

Verificar se foi criado:

```sql
\l
```

---

## 👤 3. Criar usuário

```sql
CREATE USER poo_user WITH PASSWORD '123456';
```

---

## 🔐 4. Conceder permissões

```sql
ALTER ROLE poo_user CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE poo_two_project_db TO poo_user;
```

---

## 📦 5. Dependência Maven

Adicionar no `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

---

## ▶️ 6. Executar o projeto

```bash
mvn spring-boot:run
```

---

## ✅ Resultado esperado

* Aplicação sobe sem erros
* Conexão com banco estabelecida
* Hibernate cria tabelas automaticamente (se houver entidades)

---

## ⚠️ Possíveis problemas

### ❌ PostgreSQL não está rodando

```bash
sudo systemctl start postgresql
```

### ❌ Porta diferente

Verifique se está usando a porta correta (padrão: 5432)

### ❌ Usuário ou senha incorretos

Revise as credenciais no `application.properties`

---

## 💡 Observação

Este setup é ideal para desenvolvimento. Para produção:

* Use variáveis de ambiente
* Configure migrations (Flyway ou Liquibase)
* Não utilize senha simples

---
