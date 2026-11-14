# Contribuindo para a GeekHub API

Obrigado por contribuir com a **GeekHub API** (Grupo **GuildCode**)! Este guia define o fluxo de trabalho no Git, padrões de arquitetura e critérios de validação antes de abrir um Pull Request.

---

## 1. Fluxo de Branches

Crie sempre uma nova branch a partir da branch principal atualizada seguindo o formato:

```text
<tipo>/<identificador-opcional>-<descricao-curta>
```

### Regras de Formatação
- Use apenas **letras minúsculas (lowercase)**.
- Separe as palavras com **hífens (`-`)** — nunca use espaços ou sublinhados (`_`).
- Evite acentos ou caracteres especiais.
- Mantenha o nome curto e descritivo.

### Tipos Aceitos
- `feat/`: novas funcionalidades (`feat/crud-produtos`, `feat/GH-12-carrinho`)
- `fix/`: correção de bugs em desenvolvimento (`fix/validacao-preco-produto`)
- `hotfix/`: correção urgente em produção (`hotfix/falha-geracao-token`)
- `chore/`: tarefas técnicas, dependências ou configurações (`chore/atualiza-spring-boot`)
- `docs/`: alterações em documentação (`docs/atualiza-readme`)
- `refactor/`: refatoração sem mudança de comportamento (`refactor/extrai-mapper-pedido`)
- `test/`: criação ou ajuste de testes (`test/cobertura-usuario`)
- `release/`: preparação de nova versão (`release/v0.2.0`)

---

## 2. Conventional Commits

Todas as mensagens de commit devem seguir o padrão:

```text
<tipo>(<escopo-opcional>): <descrição curta no imperativo em minúsculas>
```

### Tipos Válidos
`feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `style`, `ci`

### Exemplos
- `feat(produto): implementar cadastro e listagem paginada de produtos`
- `fix(usuario): corrigir validacao de email duplicado na edicao`
- `chore(flyway): adicionar migration v2 para tabela de produtos`
- `test(usuario): garantir 100% de cobertura JaCoCo em listar usuarios`

---

## 3. Conventional PRs

- **Título do PR**: deve seguir o mesmo padrão do Conventional Commit (ex: `feat(produto): implementar CRUD de produtos`).
- **Corpo padrão**:

```markdown
## Resumo
- Breve explicação do objetivo da entrega.

## Alterações
- Endpoints, serviços, entidades ou migrations criados/alterados.

## Verificação
- [ ] `mvnw test` passando com 100% de cobertura no JaCoCo
- [ ] Endpoints testados via Postman / Swagger
- [ ] Collection do Postman atualizada (3 exemplos por endpoint)
```

---

## 4. Padrões de Arquitetura Obrigatórios

Antes de codificar, consulte o guia completo em [AGENTS.md](AGENTS.md). Pontos essenciais:

- **Pacotes (`camada > entidade > classe`)**: `domain`, `repository`, `openapi`, `controller` (`request`/`response`), `mapper`, `validator`, `service` (`validator/`).
- **Sem `@Autowired`**: utilize campos `private final` com construtor explícito no topo da classe.
- **Sem tipos primitivos**: utilize sempre Wrappers (`Long`, `Integer`, `Boolean`, `BigDecimal`, `Double`).
- **DTOs**: sempre `record` com `@Builder` exclusivos por ação/endpoint.
- **Validators na Service**: validadores nunca são chamados no Controller.
- **Controller + OpenAPI**: implementa `*ControllerOpenApi` sem `@Override` e declara `@ResponseStatus(...)` em todo método.
- **Flyway**: toda alteração de tabela/coluna precisa de uma migration versionada `V<N>__<descricao>.sql` compatível com `ddl-auto=validate`.

---

## 5. Checklist Antes do PR

1. Execute os testes e garanta **0** falhas:
   ```bash
   cd geekhub
   ./mvnw test       # Linux / Dev Container
   .\mvnw.cmd test   # Windows
   ```
2. Confirme em `geekhub/target/site/jacoco/jacoco.csv` que todas as classes em `service`, `validator`, `mapper` e `GlobalExceptionHandler` estão com **100% de cobertura** (`0` misses).
3. Atualize e valide o JSON de `geekhub/postman/geekhub.postman_collection.json` (1 pasta por recurso, 1 request por ação e 3 exemplos de resposta por request).
