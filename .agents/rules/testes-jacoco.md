---
trigger: glob
globs: "**/src/test/**/*.java"
---

# Regras de Testes e Cobertura (`geekhub-api`)

1. **Meta de cobertura**: `100%` (0 linhas, branches, instruções e métodos perdidos) em `service`, `validator`, `mapper` e `GlobalExceptionHandler` no relatório `target/site/jacoco/jacoco.csv`.
2. **Padrão de escrita (unitários)**:
   - Usar `@ExtendWith(MockitoExtension.class)` com `@InjectMocks` e `@Mock`.
   - Todo método de teste deve ter `@DisplayName("...")` descritivo em português.
   - Estruturar cada teste com comentários `// Arrange`, `// Act` e `// Assert`.
3. **Testes de domínio (`domain/<entidade>/<Entidade>Test`)**: como `**/domain/**` é excluído do relatório do JaCoCo no `pom.xml`, toda `@Entity` com métodos de cálculo, derivação ou regra própria deve ter teste unitário dedicado cobrindo caminhos normais e extremos (`null`, coleções vazias, arredondamento).
4. **Testes de repositório (`*RepositoryTest`)**:
   - Criar testes **apenas para consultas personalizadas escritas manualmente com `@Query`** (JPQL ou SQL nativa com `nativeQuery = true`) — métodos derivados do Spring Data JPA (`findBy...`, `existsBy...`) **não** precisam ser testados.
   - Usar `@DataJpaTest`, `@AutoConfigureTestDatabase(replace = NONE)` e `@ActiveProfiles("test")` (executando em H2 modo MySQL via `application-test.properties`, isolado do banco de dev) em `src/test/java/br/com/guildcode/geekhub/repository/<entidade>/<Entidade>RepositoryTest.java`.
   - Cobrir cenário positivo e negativo/vazio seguindo `@DisplayName("...")` em português e comentários `// Arrange`, `// Act` e `// Assert`.
5. **Factories**: montar dados de teste usando classes `*Factory` em `src/test/java/br/com/guildcode/geekhub/factory/<entidade>/`.
6. **Cobertura completa de branches**: sempre testar tanto o caminho de sucesso quanto cada lançamento de exceção (`NegocioException`, `ConflitoException`, `RecursoNaoEncontradoException`, etc.) e campos opcionais/nulos.
7. **Validação**: executar `mvnw test` e inspecionar `target/site/jacoco/jacoco.csv` antes de finalizar.
