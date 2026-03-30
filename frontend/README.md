# Frontend — Benefícios

Aplicação Angular 17 que consome a API REST do backend para gerenciamento de benefícios.

---

## Pré-requisitos

- Node.js 20.x LTS
- npm 9+
- Backend rodando em `http://localhost:8080`

---

## Como executar

```bash
# Instalar dependências
npm install

# Rodar em modo desenvolvimento (com proxy para o backend)
npm start
```

Acesse **http://localhost:4200**

> O arquivo `proxy.conf.json` redireciona automaticamente as chamadas `/api` para `http://localhost:8080`, sem necessidade de configurar CORS.

---

## Build de produção

```bash
npm run build
```

Os arquivos gerados ficam em `dist/frontend/`.

---

## Testes

```bash
npm test
```

Roda os testes unitários com Jasmine/Karma no Chrome Headless.

---

## Estrutura

```
src/app/
├── models/
│   └── beneficio.model.ts          ← interfaces Beneficio e TransferRequest
├── services/
│   └── beneficio.service.ts        ← chamadas HTTP para a API
└── components/
    ├── beneficio-list/             ← listagem com editar/excluir
    ├── beneficio-form/             ← formulário criar/editar
    └── transfer/                   ← formulário de transferência
```

---

## Rotas

| URL | Tela |
|-----|------|
| `/` | Redireciona para lista |
| `/#/beneficios` | Lista de benefícios |
| `/#/beneficios/novo` | Formulário de criação |
| `/#/beneficios/:id/editar` | Formulário de edição |
| `/#/transferencia` | Transferência entre benefícios |

---

## Funcionalidades

- **Listagem** — exibe todos os benefícios com nome, descrição, valor e status
- **Criar** — formulário com validação (nome obrigatório, valor > 0)
- **Editar** — carrega dados existentes e permite alteração
- **Excluir** — confirmação antes de deletar
- **Transferência** — move valor entre dois benefícios com feedback de erro (saldo insuficiente, conflito)

