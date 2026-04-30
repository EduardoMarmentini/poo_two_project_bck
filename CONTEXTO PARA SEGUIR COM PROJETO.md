# 📦 Projeto Backend — Sistema de Estoque (Spring Boot)

## 👨‍💻 Contexto do Projeto

Sistema WEB para gerenciamento de estoque com:

* Cadastro de mercadorias
* Cadastro de fornecedores
* Controle de quantidade em estoque
* Relacionamento entre fornecedor e mercadorias

Arquitetura utilizada:

* Java 21
* Spring Boot
* PostgreSQL
* Padrão MVC (Controller, Service, Repository, DTO, Mapper)

---

# ✅ FASES CONCLUÍDAS

## 🟢 Fase 1 — Setup do Projeto

✔ Projeto Spring Boot criado
✔ Java 21 configurado
✔ Dependências instaladas (JPA, Web, PostgreSQL, Lombok)
✔ API rodando na porta 8080
✔ Conexão com banco funcionando

---

## 🟢 Fase 2 — Modelagem

✔ Entidade `Fornecedor` criada
✔ Entidade `Mercadoria` criada
✔ Relacionamento `ManyToOne` (Mercadoria → Fornecedor)
✔ Endereço implementado como objeto embutido (sem tabela separada)

---

## 🟢 Fase 3 — Banco de Dados

✔ Tabelas criadas no PostgreSQL
✔ Estrutura consistente com entidades
✔ Banco populado com dados realistas
✔ Testes básicos realizados

---

## 🟢 Fase 4 — DTO + Mapper

✔ DTOs criados:

* `FornecedorRequestDTO`
* `FornecedorResponseDTO`
* `MercadoriaRequestDTO`
* `MercadoriaResponseDTO`

✔ Mapper implementado:

* `FornecedorMapper`
* `MercadoriaMapper`

✔ Separação correta:

* Entity ≠ DTO

---

## 🟢 Fase 5 — Service (Fornecedor)

✔ CRUD completo de fornecedor
✔ Uso de DTO + Mapper
✔ Tratamento de erro com exception
✔ Atualização de dados incluindo endereço

---

## 🟢 Fase 6 — Controller (Fornecedor)

✔ Endpoints REST implementados:

```
POST   /fornecedores
GET    /fornecedores
GET    /fornecedores/{id}
PUT    /fornecedores/{id}
DELETE /fornecedores/{id}
```

✔ Uso de `ResponseEntity`
✔ Status HTTP corretos (200, 201, 204)

---

## 🟢 Fase 7 — Validação e Tratamento de Erros

✔ Bean Validation (`@NotBlank`, `@NotNull`, etc)
✔ Uso de `@Valid` nos controllers
✔ Exception customizada:

* `NotFoundException`

✔ Handler global:

* `@RestControllerAdvice`

✔ Respostas padronizadas:

```json
{
  "status": 400,
  "timestamp": "...",
  "errors": { ... }
}
```

---

# ⚠️ FASES PENDENTES (RESPONSABILIDADE DO TIME)

---

# 🟡 Fase 5.1 — Service de Mercadoria

## 🎯 Objetivo

Criar a lógica de negócio para mercadorias

## 📌 O que deve ser feito

### ✔ Criar:

`MercadoriaService.java`

### ✔ Implementar métodos:

* `criar(MercadoriaRequestDTO dto)`
* `listar()`
* `buscarPorId(Long id)`
* `atualizar(Long id, MercadoriaRequestDTO dto)`
* `deletar(Long id)`
* `listarPorFornecedor(Long fornecedorId)`

---

## ⚠️ Regras OBRIGATÓRIAS

### 🔥 1. Mercadoria deve ter fornecedor válido

```java
fornecedorRepository.findById(dto.getFornecedorId())
    .orElseThrow(() -> new NotFoundException("Fornecedor não encontrado"));
```

---

### 🔥 2. NÃO listar mercadorias com estoque 0

```java
.filter(m -> m.getQuantidade() > 0)
```

---

### 🔥 3. Usar Mapper (OBRIGATÓRIO)

```java
MercadoriaMapper.toEntity(...)
MercadoriaMapper.toResponse(...)
```

---

### 🔥 4. Usar NotFoundException

❌ NÃO usar:

```java
new RuntimeException()
```

✔ usar:

```java
new NotFoundException()
```

---

# 🟡 Fase 6.1 — Controller de Mercadoria

## 🎯 Objetivo

Expor endpoints REST para mercadorias

---

## 📌 Criar:

`MercadoriaController.java`

---

## 📌 Endpoints obrigatórios:

```
POST   /mercadorias
GET    /mercadorias
GET    /mercadorias/{id}
PUT    /mercadorias/{id}
DELETE /mercadorias/{id}
GET    /mercadorias/fornecedor/{fornecedorId}
```

---

## ⚠️ Regras importantes

### ✔ Usar @Valid

```java
@RequestBody @Valid MercadoriaRequestDTO dto
```

---

### ✔ Retornar status correto

* POST → 201
* GET → 200
* DELETE → 204

---

### ✔ Usar Service (NUNCA acessar repository direto)

---

# 🟡 Fase 7.1 — Ajustes de Validação em Mercadoria

## 🎯 Objetivo

Garantir que validações funcionem igual fornecedor

---

## 📌 Validar no DTO:

```java
@NotBlank(message = "Nome é obrigatório")
private String nome;

@NotNull(message = "Quantidade é obrigatória")
@Min(value = 0, message = "Quantidade não pode ser negativa")
private Integer quantidade;

@NotNull(message = "Fornecedor é obrigatório")
private Long fornecedorId;
```

---

## 📌 Garantir:

✔ erro 400 quando inválido
✔ erro 404 quando fornecedor não existe
✔ erro padronizado via GlobalExceptionHandler

---

# 🧠 REGRAS DO TRABALHO IMPLEMENTADAS

✔ CRUD completo
✔ Separação MVC
✔ Uso de DTO
✔ Validação de dados
✔ Tratamento de erros
✔ Relacionamento entre entidades
✔ Filtro de estoque (não mostrar quantidade 0)
✔ Listagem por fornecedor

---

# 🚀 STATUS DO PROJETO

## 🔥 Backend

✔ 90% concluído
❗ Falta apenas mercadoria (service + controller)

---

# 💬 INSTRUÇÃO PARA USO COM IA

Caso alguém do grupo use IA:

👉 utilizar como contexto:

* estrutura já existente
* padrão de código já aplicado
* uso de DTO + Mapper
* uso de NotFoundException
* uso de validação

❗ IMPORTANTE:
**NÃO alterar código existente — apenas complementar**

---

# 🏁 CONCLUSÃO

O projeto já possui base sólida e arquitetura correta.

As próximas implementações devem seguir exatamente o mesmo padrão já estabelecido para fornecedor.

---
