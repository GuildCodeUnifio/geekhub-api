---
trigger: glob
globs: "**/src/test/**/*.java"
---

# Regras de Testes Unitários e Cobertura (`geekhub-api`)

1. **Meta de cobertura**: `100%` (0 linhas, branches, instruções e métodos perdidos) em `service`, `validator`, `mapper` e `GlobalExceptionHandler` no relatório `target/site/jacoco/jacoco.csv`.
2. **Padrão de escrita**:
   - Usar `@ExtendWith(MockitoExtension.class)` com `@InjectMocks` e `@Mock`.
   - Todo método de teste deve ter `@DisplayName("...")` descritivo em português.
   - Estruturar cada teste com comentários `// Arrange`, `// Act` e `// Assert`.
3. **Factories**: montar dados de teste usando classes `*Factory` em `src/test/java/br/com/guildcode/geekhub/factory/<entidade>/`.
4. **Cobertura completa de branches**: sempre testar tanto o caminho de sucesso quanto cada lançamento de exceção (`NegocioException`, `ConflitoException`, `RecursoNaoEncontradoException`, etc.) e campos opcionais/nulos.
5. **Validação**: executar `mvnw test` e inspecionar `target/site/jacoco/jacoco.csv` antes de finalizar.
