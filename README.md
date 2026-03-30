# Desafio Fullstack Integrado — Benefícios

Solução completa em camadas: banco de dados → EJB → Spring Boot → Angular.

---

## Arquitetura

```
┌──────────┐     HTTP/REST      ┌──────────────────┐     JPA/H2      ┌──────────┐
│  Angular │ ────────────────▶  │  Spring Boot API  │ ──────────────▶ │    DB    │
│ :4200    │ ◀────────────────  │  :8080            │                 │  (H2)    │
└──────────┘                    └──────────────────┘                 └──────────┘
                                        │
                              Lógica de negócio
                              replicada do EJB
                              (BeneficioService)
```

> O módulo `ejb-module` contém a implementação original com Jakarta EE / EJB.
> O `backend-module` replica a lógica corrigida em um `@Service` Spring,
> tornando o projeto executável sem servidor de aplicação Jakarta EE.

---

## Como executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Node.js 20+ e npm

### 1. Backend
```bash
cd backend-module
mvn spring-boot:run
```
A API sobe em **http://localhost:8080**.  
O banco H2 em memória é criado automaticamente com os dados de `schema.sql` + `data.sql`.

**Swagger UI:** http://localhost:8080/swagger-ui.html  
**H2 Console:** http://localhost:8080/h2-console  
*(JDBC URL: `jdbc:h2:mem:beneficiodb`, user: `sa`, sem senha)*

### 2. Frontend
```bash
cd frontend
npm install
npm start
```
A aplicação sobe em **http://localhost:4200** e faz proxy das chamadas `/api` para o backend.

---

## Endpoints da API

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/api/v1/beneficios` | Listar todos |
| GET | `/api/v1/beneficios/{id}` | Buscar por ID |
| POST | `/api/v1/beneficios` | Criar novo |
| PUT | `/api/v1/beneficios/{id}` | Atualizar |
| DELETE | `/api/v1/beneficios/{id}` | Remover |
| POST | `/api/v1/beneficios/transfer` | Transferir valor |

Documentação completa disponível no Swagger UI após subir o backend.

---

## Bug corrigido no EJB

### O problema original (`BeneficioEjbService`)
```java
// BUG: sem validações, sem locking, pode gerar saldo negativo e lost update
from.setValor(from.getValor().subtract(amount));
to.setValor(to.getValor().add(amount));
```
Três falhas críticas:
1. **Sem verificação de existência** — NullPointerException se o ID não existir
2. **Sem validação de saldo** — valor podia ficar negativo
3. **Sem locking** — duas transações simultâneas podiam debitar o mesmo saldo (lost update)

### A correção aplicada
```java
// 1. Valida existência com LockModeType.OPTIMISTIC_FORCE_INCREMENT
Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
Beneficio to   = em.find(Beneficio.class, toId,   LockModeType.OPTIMISTIC_FORCE_INCREMENT);

// 2. Verifica nulos
if (from == null) throw new IllegalArgumentException("Origem não encontrada");
if (to   == null) throw new IllegalArgumentException("Destino não encontrado");

// 3. Verifica saldo antes de qualquer mutação
if (from.getValor().compareTo(amount) < 0)
    throw new InsufficientBalanceException(fromId);  // RuntimeException → rollback automático

from.setValor(from.getValor().subtract(amount));
to.setValor(to.getValor().add(amount));
```

**`@Version`** na entidade garante que o JPA detecte escritas concorrentes e lance
`OptimisticLockException`, revertendo automaticamente a transação — eliminando o lost update.

---

## Testes

### Backend
```bash
cd backend-module
mvn test
```
- `BeneficioServiceTest` — testes unitários com Mockito (CRUD + transfer)
- `BeneficioControllerIntegrationTest` — testes de integração com MockMvc + H2

### EJB
```bash
cd ejb-module
mvn test
```
- `BeneficioEjbServiceTest` — verifica validações, locking e rollback

### Frontend
```bash
cd frontend
npm test
```
- Testes de serviço, lista e formulários com Jasmine/Karma

---

## Estrutura do projeto

```
bipTesteIntegrado/
├── db/
│   ├── schema.sql          # DDL da tabela BENEFICIO (com coluna VERSION)
│   └── seed.sql            # Dados iniciais
├── ejb-module/
│   └── src/main/java/com/example/ejb/
│       ├── Beneficio.java               # Entidade JPA com @Version
│       ├── BeneficioEjbService.java     # Bug corrigido
│       └── InsufficientBalanceException.java
├── backend-module/
│   └── src/main/java/com/example/backend/
│       ├── Beneficio.java
│       ├── BeneficioRepository.java
│       ├── BeneficioService.java        # Lógica corrigida em Spring
│       ├── BeneficioController.java     # CRUD + /transfer com Swagger
│       ├── BeneficioDTO.java
│       ├── TransferRequestDTO.java
│       ├── GlobalExceptionHandler.java
│       ├── SwaggerConfig.java
│       └── InsufficientBalanceException.java
├── frontend/
│   └── src/app/
│       ├── models/beneficio.model.ts
│       ├── services/beneficio.service.ts
│       └── components/
│           ├── beneficio-list/   # Tabela com editar/excluir
│           ├── beneficio-form/   # Formulário criar/editar
│           └── transfer/         # Formulário de transferência
└── .github/workflows/ci.yml      # CI: backend tests + frontend build
```

---

## Critérios atendidos

| Critério | Status |
|----------|--------|
| Arquitetura em camadas (DB → EJB → Backend → Frontend) | ✅ |
| Correção do bug EJB (validação + locking otimista) | ✅ |
| CRUD completo + endpoint de transferência | ✅ |
| Qualidade de código (DTOs, exceptions, exception handler) | ✅ |
| Testes unitários e de integração | ✅ |
| Documentação Swagger + README | ✅ |
| Frontend Angular funcional | ✅ |
