---
name: novo-endpoint
description: Cria uma nova entidade ou endpoint completo na geekhub-api seguindo 100% da arquitetura do projeto (Flyway, Domain, Repository, DTOs, Validators na Service, Mapper, Controller+OpenApi, Testes 100% JaCoCo e Postman). Use quando o usuário pedir para criar nova funcionalidade, entidade, CRUD ou endpoint, ou ao digitar /novo-endpoint.
---

# Criar Nova Entidade / Endpoint (`/novo-endpoint`)

Siga esta ordem exata para manter a arquitetura **camada > entidade > classe** do projeto. Use o módulo de usuários (`UsuarioController`, `CadastrarUsuarioService`, `CadastrarUsuarioMapper`, `UsuarioControllerOpenApi`) como referência viva.

---

## Passo 1: Banco de Dados e Domínio (se houver nova tabela/coluna)
1. Crie ou incremente a migration em `geekhub/src/main/resources/db/migration/V<N>__<descricao>.sql` (ou ative `/nova-migracao`).
2. Crie ou atualize a entidade em `domain/<entidade>/<Entidade>.java` usando apenas **Wrapper types** (`Long`, `Integer`, `Boolean`, `BigDecimal`), Lombok (`@Builder`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(of = "id")`) e mapeamento 100% compatível com `ddl-auto=validate`.
3. Crie o repositório em `repository/<entidade>/<Entidade>Repository.java` estendendo `JpaRepository<<Entidade>, Long>`.

---

## Passo 2: DTOs (`record` com `@Builder`)
1. Em `controller/<entidade>/request/<Acao><Entidade>Request.java`:
   - `record` público com `@Builder`, anotações Jakarta Validation (`@NotBlank`, `@NotNull`, `@Size`, `@Positive`, etc.) e tipos Wrapper.
2. Em `controller/<entidade>/response/<Acao><Entidade>Response.java`:
   - `record` público com `@Builder` exclusivo para o retorno dessa ação.

---

## Passo 3: Validadores e Mappers
1. **Validação de entrada** (quando necessário): `validator/<entidade>/<Acao><Entidade>Validator.java` (`@Component`), lançando `NegocioException`.
2. **Validação de regra de negócio**: `service/<entidade>/validator/<Regra>ValidatorService.java` (`@Service`), lançando `ConflitoException` (`409`), `RecursoNaoEncontradoException` (`404`) ou `NegocioException` (`400`).
3. **Mapper exclusivo da ação**: `mapper/<entidade>/<Acao><Entidade>Mapper.java` (`@Component`), convertendo `Request -> Entidade` e `Entidade -> Response` com `.builder()...build()`.

---

## Passo 4: Service e Controller
1. **Service** em `service/<entidade>/<Acao><Entidade>Service.java`:
   - `@Service`, campos `private final` com construtor explícito no topo (sem `@Autowired`).
   - Chama os `*Validator` e `*ValidatorService` **dentro da Service**.
   - Se a ação depender do usuário logado, injete `UsuarioAutenticadoService`.
   - Para listagens paginadas, receba `Pageable` e use `.map(mapper::toResponse)`.
2. **OpenAPI** em `openapi/<entidade>/<Entidade>ControllerOpenApi.java`:
   - Interface com `@Tag`, `@Operation` e `@ApiResponses` (`200/201/204`, `400`, `401`, `403` se `ADMIN`, `404`, `409`).
3. **Controller** em `controller/<entidade>/<Entidade>Controller.java`:
   - Implementa `<Entidade>ControllerOpenApi` **sem `@Override`**.
   - Injeta apenas `*Service` via construtor (nunca `*Validator` ou `*Repository`).
   - Declara `@ResponseStatus(...)` em todo método.
   - Listagens usam `@PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable`.
   - Endpoints administrativos recebem `@PreAuthorize("hasAuthority('SCOPE_ADMIN')")`.
   - Se o endpoint for público, libere em `SecurityConfig`.

---

## Passo 5: Testes (100% JaCoCo) e Postman
1. Crie Factories em `src/test/java/br/com/guildcode/geekhub/factory/<entidade>/`.
2. Crie testes unitários (`@ExtendWith(MockitoExtension.class)`, `@DisplayName` em português, comentários `// Arrange`, `// Act`, `// Assert`) cobrindo **100%** das Services, Validators e Mappers.
3. Atualize `geekhub/postman/geekhub.postman_collection.json`: 1 pasta para o recurso, 1 request por ação, **3 exemplos de resposta** por request, usando `https://picsum.photos/200` para URLs de imagem.
4. Execute `/validar-entrega` para confirmar build, testes, `jacoco.csv` (100%) e JSON do Postman.
