# CampusGigs — Projeto Diamante

Plataforma de freelas entre estudantes universitários desenvolvida com **Spring Boot**, **Spring Security (JWT)**, **Flyway**, **Docker Compose** e **Spring HttpExchange**.

---

## 1. Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.4**
- **Spring Security** com autenticação stateless e tokens **JWT (jjwt 0.12.6)**
- **Spring Data JPA** e **Hibernate**
- **Flyway** para versionamento e migrações do banco de dados
- **PostgreSQL 16** via **Docker Compose**
- **Spring HttpExchange** (cliente HTTP declarativo do Spring Framework) para integração com ViaCEP
- **Lombok** e **Bean Validation (Jakarta Validation)**

---

## 2. Instruções de Execução

### Pré-requisitos
- **Docker** e **Docker Compose** instalados e em execução.
- **Java 21 (JDK)** instalado (ou uso do wrapper Gradle incluso).

### 2.1 Subindo o Banco de Dados com Docker
O projeto conta com o `compose.yaml` configurado. Para iniciar o banco de dados PostgreSQL na porta padrão `5432`:

```bash
docker compose up -d postgres
```

> **Nota:** Com a dependência `spring-boot-docker-compose` presente no projeto, executar o comando `bootRun` no ambiente de desenvolvimento gerencia e conecta automaticamente os serviços do `compose.yaml`.

### 2.2 Executando a Aplicação Localmente

No Linux/macOS:
```bash
./gradlew bootRun
```

No Windows (PowerShell / Prompt de Comando):
```powershell
.\gradlew.bat bootRun
```

A API estará disponível em `http://localhost:8080`.

### 2.3 Executando a Suíte de Testes Automatizados

```powershell
.\gradlew.bat test
```

---

## 3. Usuário Administrador Padrão

O projeto inclui a migração Flyway `V2__seed_admin.sql` que inicializa uma conta administrativa para testes de permissão por papel:

- **E-mail:** `admin@campusgigs.br`
- **Senha:** `admin123`
- **Papel:** `ADMIN`

---

## 4. Principais Endpoints da API

### Autenticação & Usuários
- `POST /usuarios` — Cadastra um novo usuário aluno. Caso o CEP seja informado, a API consulta o ViaCEP de forma declarativa e preenche cidade e UF automaticamente.
- `POST /auth/login` — Autentica o usuário e retorna o token JWT no formato `Bearer`.
- `GET /usuarios/me` — Retorna os dados do usuário autenticado no momento.
- `PATCH /usuarios/me/cep` — Atualiza o CEP do usuário logado, reconsultando cidade e UF no serviço externo.

### Serviços (Freelas)
- `POST /servicos` — Publica um novo serviço (requer autenticação; o autor torna-se o prestador).
- `GET /servicos` — Lista os serviços publicados (aberto para consulta; suporta filtros `?categoria=TI` e `?situacao=ativo`).
- `GET /servicos/{id}` — Consulta os detalhes de um serviço específico.
- `PUT /servicos/{id}` — Edita dados de um serviço (apenas o prestador dono ou usuário com papel `ADMIN`).
- `PATCH /servicos/{id}/encerrar` — Encerra um serviço publicado (apenas o prestador dono ou usuário com papel `ADMIN`).

### Contratações
- `POST /contratacoes` — Contrata um serviço ativo (requer autenticação; proibido contratar o próprio serviço ou serviços inativos).
- `GET /contratacoes` — Lista as contratações relacionadas ao usuário logado (usuário `ADMIN` visualiza todas).
- `GET /contratacoes/{id}` — Exibe detalhes de uma contratação específica.
- `PATCH /contratacoes/{id}/situacao` — Altera a situação da contratação (`solicitada`, `aceita`, `concluida`, `cancelada`).

---

## 5. Exemplos de Chamadas e Evidências de Testes Manuais

### 5.1 Cadastro de Usuário com Integração Declarativa de CEP (HttpExchange)

**Requisição:**
```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Lucas Silva",
    "email": "lucas@campusgigs.br",
    "senha": "senha123",
    "cep": "01310-100"
  }'
```

**Resposta (HTTP 201 Created):**
```json
{
  "id": 2,
  "nome": "Lucas Silva",
  "email": "lucas@campusgigs.br",
  "cep": "01310100",
  "cidade": "São Paulo",
  "uf": "SP",
  "papel": "USER"
}
```

---

### 5.2 Autenticação (Login) e Obtenção do Token JWT

**Requisição:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "lucas@campusgigs.br",
    "senha": "senha123"
  }'
```

**Resposta (HTTP 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdWNhc0BjYW1wdXNnaWdzLmJyIiwiaWQiOjIsInBhcGVsIjoiVVNFUiIsImlhdCI6MTY5...",
  "tipo": "Bearer",
  "usuario": {
    "id": 2,
    "nome": "Lucas Silva",
    "email": "lucas@campusgigs.br",
    "cep": "01310100",
    "cidade": "São Paulo",
    "uf": "SP",
    "papel": "USER"
  }
}
```

---

### 5.3 Publicação de um Freela (Serviço)

**Requisição com Token:**
```bash
curl -X POST http://localhost:8080/servicos \
  -H "Authorization: Bearer <TOKEN_LUCAS>" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Aulas de Estrutura de Dados em Java",
    "descricao": "Reforço para provas de POO e grafos",
    "categoria": "Monitoria",
    "preco": 60.00
  }'
```

**Resposta (HTTP 201 Created):**
```json
{
  "id": 1,
  "prestadorId": 2,
  "prestadorNome": "Lucas Silva",
  "prestadorEmail": "lucas@campusgigs.br",
  "titulo": "Aulas de Estrutura de Dados em Java",
  "descricao": "Reforço para provas de POO e grafos",
  "categoria": "Monitoria",
  "preco": 60.00,
  "situacao": "ativo",
  "criadoEm": "2026-09-10T13:20:00"
}
```

---

### 5.4 Contratação de Serviço por Outro Aluno

**Requisição (Autenticado como Mariana, ID 3):**
```bash
curl -X POST http://localhost:8080/contratacoes \
  -H "Authorization: Bearer <TOKEN_MARIANA>" \
  -H "Content-Type: application/json" \
  -d '{
    "servicoId": 1
  }'
```

**Resposta (HTTP 201 Created):**
```json
{
  "id": 1,
  "servicoId": 1,
  "servicoTitulo": "Aulas de Estrutura de Dados em Java",
  "prestadorId": 2,
  "prestadorNome": "Lucas Silva",
  "contratanteId": 3,
  "contratanteNome": "Mariana Souza",
  "situacao": "solicitada",
  "criadoEm": "2026-09-10T13:25:00"
}
```

---

### 5.5 Regra de Negócio: Proibição de Contratar o Próprio Serviço

**Requisição (Lucas tentando contratar seu próprio serviço ID 1):**
```bash
curl -X POST http://localhost:8080/contratacoes \
  -H "Authorization: Bearer <TOKEN_LUCAS>" \
  -H "Content-Type: application/json" \
  -d '{
    "servicoId": 1
  }'
```

**Resposta (HTTP 400 Bad Request):**
```json
{
  "timestamp": "2026-09-10T13:26:00",
  "status": 400,
  "erro": "Não é permitido contratar o próprio serviço"
}
```

---

### 5.6 Evidência de Acesso Negado por Papel (HTTP 403 Forbidden)

**Cenário:** O aluno Mariana (`USER`) tenta encerrar o freela pertencente a Lucas:

**Requisição com Token de Usuário Comum:**
```bash
curl -X PATCH http://localhost:8080/servicos/1/encerrar \
  -H "Authorization: Bearer <TOKEN_MARIANA>"
```

**Resposta (HTTP 403 Forbidden):**
```json
{
  "timestamp": "2026-09-10T13:27:00",
  "status": 403,
  "erro": "Acesso negado"
}
```

**Cenário:** O usuário com papel `ADMIN` executa o encerramento do mesmo freela de terceiros:

**Requisição com Token de Administrador:**
```bash
curl -X PATCH http://localhost:8080/servicos/1/encerrar \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

**Resposta (HTTP 200 OK):**
```json
{
  "id": 1,
  "prestadorId": 2,
  "prestadorNome": "Lucas Silva",
  "prestadorEmail": "lucas@campusgigs.br",
  "titulo": "Aulas de Estrutura de Dados em Java",
  "descricao": "Reforço para provas de POO e grafos",
  "categoria": "Monitoria",
  "preco": 60.00,
  "situacao": "encerrado",
  "criadoEm": "2026-09-10T13:20:00"
}
```

---

## 6. Justificativa das Decisões por Checkpoint

- **CP1:** Ambiente configurado com Docker Compose e PostgreSQL na porta padrão 5432, associado à biblioteca `spring-boot-docker-compose` e Flyway para controle declarativo do schema inicial.
- **CP2:** Cadastro e autenticação seguros usando hash BCrypt para senhas e validação de credenciais centralizada sem expor informações de infraestrutura.
- **CP3:** Emissão e validação de tokens JWT (HS256) em filtro customizado `OncePerRequestFilter`, garantindo autenticação prévia em rotas restritas e extração de claims.
- **CP4:** Modelagem do domínio de Serviços e Contratações aplicando controle estrito de permissões: apenas prestador dono ou `ADMIN` encerra freelas, bloqueando contratações do próprio serviço ou de serviços fora da situação `ativo`.
- **CP5:** Implementação de cliente declarativo Spring HTTP (`@HttpExchange` / `@GetExchange`) integrado ao ViaCEP com configuração explícita de timeout (conexão e leitura). Em caso de indisponibilidade ou latência excessiva do serviço externo, a exceção é interceptada e convertida em resposta amigável centralizada, evitando travamento ou stack traces no cliente.
