---
trigger: glob
globs: "**/src/main/**/*.java, **/src/main/resources/**"
---

# Regras de Arquitetura Spring Boot (`geekhub-api`)

Ao criar ou editar classes em `src/main`, aplique obrigatoriamente:

1. **Estrutura de pacotes**: `camada > entidade > classe` (`domain`, `repository`, `openapi`, `controller`, `controller/request`, `controller/response`, `mapper`, `validator`, `service`, `service/<entidade>/validator`).
2. **Construtor explícito**: nunca use `@Autowired`. Use campos `private final` com construtor no topo da classe.
3. **Sem primitivos**: use apenas classes Wrapper (`Long`, `Integer`, `Boolean`, `BigDecimal`, `Double`).
4. **DTOs**: todo Request e Response deve ser `record` público com `@Builder`, exclusivo para aquela ação.
5. **Validators na Service**: `*Validator` e `*ValidatorService` nunca são chamados no Controller; sempre injetados e invocados na `*Service`.
6. **1 Mapper por Service**: classe `@Component` com métodos `toEntity(...)` / `toResponse(...)` usando `.builder()...build()`.
7. **Controller + OpenApi**: Controller implementa `*ControllerOpenApi` **sem `@Override`** e define `@ResponseStatus(...)` em todo método. Listagens paginadas usam `Page<*Response>` e `@PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable`.
8. **Segurança**: endpoints exclusivos de administrador devem ter `@PreAuthorize("hasAuthority('SCOPE_ADMIN')")`.
9. **Flyway vs JPA**: como `spring.jpa.hibernate.ddl-auto=validate`, toda alteração em `@Entity` exige migration correspondente em `src/main/resources/db/migration/V<N>__<descricao>.sql`.
