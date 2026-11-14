# AGENTS.md — GeekHub API (`geekhub-api`)

Guia oficial da arquitetura, convenções de código, testes, migrações e fluxo Git do backend **GeekHub** (Projeto Integrador UNIFIO — Grupo GuildCode).

---

## 1. Stack Tecnológica

- **Linguagem / Runtime**: Java 21
- **Framework**: Spring Boot `4.1.2` (`spring-boot-starter-webmvc`, `data-jpa`, `validation`, `security`, `oauth2-resource-server`, `flyway`, `actuator`)
- **Banco de Dados**: MySQL 8 (`ecommerce`), gerenciado via **Flyway** (`flyway-mysql`)
- **Documentação OpenAPI**: SpringDoc OpenAPI `3.2.0` (`/swagger-ui.html`, `/v3/api-docs`)
- **Utilitários & Testes**: Lombok, JUnit 5, Mockito, JaCoCo `0.8.12`

---

## 2. Organização de Pacotes (`camada > entidade > classe`)

Base: `br.com.guildcode.geekhub` dentro de `geekhub/src/main/java/br/com/guildcode/geekhub/`

```text
br.com.guildcode.geekhub/
├── GeekhubApplication.java
├── config/                         # SecurityConfig, CorsConfig, OpenApiConfig, FlywayConfig
├── exception/                      # GlobalExceptionHandler + exceções de domínio
├── domain/<entidade>/              # Entidades JPA (@Entity) e Enums de domínio
├── repository/<entidade>/          # Interfaces JpaRepository
├── openapi/<entidade>/             # Interfaces *ControllerOpenApi (anotações Swagger)
├── controller/<entidade>/          # @RestController implementando *ControllerOpenApi
│   ├── request/                    # DTOs de entrada (records com @Builder)
│   └── response/                   # DTOs de saída (records com @Builder)
├── mapper/<entidade>/              # @Component para conversão DTO <-> Entidade
├── validator/<entidade>/           # Validadores de entrada chamados pelas Services
└── service/<entidade>/             # Regras de negócio (1 classe por caso de uso)
    └── validator/                  # Validators de regra de negócio (*ValidatorService)
```

---

## 3. Convenções de Código Obrigatórias

### 3.1 Nomenclatura
- O sufixo da camada fica **sempre no final** do nome da classe:
  - `*Controller`, `*ControllerOpenApi`, `*Request`, `*Response`
  - `*Service`, `*ValidatorService` (regra de negócio), `*Validator` (entrada)
  - `*Mapper`, `*Repository`, `*Factory` (testes)
- **Um DTO (`Request`/`Response`) exclusivo por endpoint/ação** (ex: `CadastrarUsuarioRequest`, `EditarUsuarioResponse`, `ListarUsuarioResponse`).
- **Um `*Mapper` exclusivo por `*Service`/ação**, evitando acoplamento entre casos de uso.

### 3.2 Injeção de Dependência e Tipos
- **Proibido `@Autowired`**: declarar dependências como `private final` com **construtor explícito no topo da classe**.
- **Proibido tipos primitivos** em entidades, DTOs, serviços e retornos (`boolean`, `int`, `long`, `double`): usar sempre Wrappers (`Boolean`, `Integer`, `Long`, `BigDecimal`, `Double`).

### 3.3 DTOs (`Request` e `Response`)
- Todos os DTOs são **`record` públicos anotados com `@Builder` (Lombok)** e `@Size` / Bean Validation alinhados às colunas do banco.
- Exemplo:
  ```java
  @Builder
  public record CadastrarUsuarioRequest(
          @NotBlank(message = "é obrigatório")
          @Size(max = 255)
          String nome
  ) {}
  ```

### 3.4 Controllers e OpenAPI
- O Controller **nunca retorna entidade** e **nunca chama `*Validator` diretamente** — chama apenas `*Service`.
- O Controller implementa `<Entidade>ControllerOpenApi` **sem `@Override`** nos métodos.
- Toda ação do Controller declara explicitamente `@ResponseStatus(...)` (`CREATED`, `OK`, `NO_CONTENT`, etc.).
- Listagens retornam **`Page<*Response>`** com `@PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable`.
- Restrição de acesso administrativo usa `@PreAuthorize("hasAuthority('SCOPE_ADMIN')")`.
- Documentação Swagger (`@Tag`, `@Operation`, `@ApiResponses`) fica isolada na interface `openapi/<entidade>/*ControllerOpenApi.java`.

### 3.5 Services e Validators
- **1 Service por caso de uso** (`CadastrarUsuarioService`, `ListarUsuariosService`, `DetalharUsuarioService`, `EditarUsuarioService`, `RemoverUsuarioService`, `BuscarUsuarioService`).
- **Validators sempre são chamados dentro da `*Service`**, nunca no Controller:
  - Validação estrutural/manual complementar: `validator/<entidade>/*Validator.java`
  - Validação de regra de negócio (ex: unicidade, posse, saldo): `service/<entidade>/validator/*ValidatorService.java`
- Exceções padronizadas tratadas pelo `GlobalExceptionHandler`:
  - `NegocioException` → `400 Bad Request`
  - `BadCredentialsException` / `AuthenticationException` → `401 Unauthorized`
  - `AccessDeniedException` (`@PreAuthorize`) → `403 Forbidden`
  - `RecursoNaoEncontradoException` → `404 Not Found`
  - `ConflitoException` / `DataIntegrityViolationException` → `409 Conflict`

### 3.6 Segurança e Autenticação
- `SecurityConfig` usa JWT assimétrico RSA (`authz.pub` / `authz.pem`) em modo `STATELESS`, `@EnableMethodSecurity` e `BCryptPasswordEncoder`.
- Claim `scope` no token contém os nomes da entidade `Permissao` (ex: `"USUARIO"`, `"ADMIN"`), que o Spring converte em authorities `SCOPE_USUARIO` e `SCOPE_ADMIN`.
- Rotas públicas atuais: `POST /login`, `POST /usuarios`, `/swagger-ui/**`, `/v3/api-docs/**`. Todas as demais exigem token JWT válido.

---

## 4. Banco de Dados e Flyway

- Scripts versionados em `geekhub/src/main/resources/db/migration/V<N>__<descricao_em_snake_case>.sql`.
- `spring.jpa.hibernate.ddl-auto=validate`: toda nova tabela/coluna criada nas entidades `@Entity` deve corresponder exatamente ao DDL da migration.
- `FlywayConfig` executa `flyway.clean()` + `flyway.migrate()` na inicialização da API para manter o banco limpo e determinístico durante o desenvolvimento.
- **Usuários iniciais (`V1`, senha `123456`)**:
  - Admin: `admin@geekhub.com` (permissões `ADMIN`, `USUARIO`)
  - Comum: `usuario@geekhub.com` (permissão `USUARIO`)

---

## 5. Testes Unitários e Cobertura JaCoCo (100%)

- Cobertura obrigatória de **100%** (instruções, branches, linhas e métodos) em todas as classes de `service`, `validator`, `mapper` e `GlobalExceptionHandler`.
- Todos os métodos de teste devem ter `@DisplayName("...")` em português e seguir o padrão **AAA comentado** (`// Arrange`, `// Act`, `// Assert`).
- Criar massas de dados reutilizáveis em `src/test/java/br/com/guildcode/geekhub/factory/<entidade>/`.
- Comando de verificação (executar dentro de `geekhub/`):
  - Windows: `.\mvnw.cmd test`
  - Linux / Dev Container: `./mvnw test`
- Relatório gerado em `geekhub/target/site/jacoco/jacoco.csv`.

---

## 6. Collection do Postman

- Arquivo: `geekhub/postman/geekhub.postman_collection.json`.
- Estrutura: **1 pasta por recurso** (`usuarios`, `login`, ...), **1 request por endpoint** e **3 exemplos salvos (`response`)** por request (1 sucesso + 2 erros principais).
- URLs de imagem de exemplo usam `https://picsum.photos/200`.

---

## 7. Fluxo Git: Branches, Commits e Pull Requests

### 7.1 Nomeação de Branches
Estrutura padrão: `<tipo>/<identificador-opcional>-<descricao-curta>`
- Sempre em **minúsculas (lowercase)**, palavras separadas por **hífens (`-`)**, sem acentos, espaços ou sublinhados (`_`).
- Tipos aceitos: `feat/`, `fix/`, `hotfix/`, `chore/`, `docs/`, `refactor/`, `test/`, `release/`.
- Exemplos: `feat/crud-produtos`, `feat/GH-12-carrinho-compras`, `fix/validacao-preco-produto`, `docs/diagrama-pedidos`.

### 7.2 Conventional Commits
Formato: `<tipo>(<escopo-opcional>): <descrição curta no imperativo em minúsculas>`
- Tipos: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `style`, `ci`.
- Exemplos:
  - `feat(produto): implementar cadastro e listagem paginada de produtos`
  - `fix(usuario): corrigir validacao de email na edicao`
  - `test(pedido): adicionar testes unitarios de criacao de pedido`

### 7.3 Conventional PRs
- **Título**: mesmo padrão do Conventional Commit (`feat(produto): implementar CRUD de produtos`).
- **Corpo padrão**:
  ```markdown
  ## Resumo
  - Breve explicação do objetivo do PR.

  ## Alterações
  - Principais arquivos, endpoints e migrations adicionados.

  ## Verificação
  - `mvnw test` (100% JaCoCo)
  - Validação via Postman / Swagger / MySQL
  ```
