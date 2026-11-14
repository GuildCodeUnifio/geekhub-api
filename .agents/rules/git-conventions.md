---
trigger: always_on
---

# Padrões de Git: Branches, Conventional Commits e Conventional PRs

Sempre que criar branches, sugerir mensagens de commit ou estruturar Pull Requests no repositório `geekhub-api`, siga estas convenções:

## 1. Nome de Branch
Formato: `<tipo>/<identificador-opcional>-<descricao-curta>`
- Sempre em **lowercase**, separado por **hífens (`-`)**, sem acentos, espaços ou `_`.
- Tipos: `feat/`, `fix/`, `hotfix/`, `chore/`, `docs/`, `refactor/`, `test/`, `release/`.
- Exemplos: `feat/cadastro-produtos`, `feat/GH-14-checkout`, `fix/erro-token-expirado`, `chore/flyway-v2`.

## 2. Conventional Commits
Formato: `<tipo>(<escopo-opcional>): <descrição curta em minúsculas sem ponto final>`
- Tipos: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `style`, `ci`.
- Exemplos: `feat(produto): criar endpoint de listagem paginada`, `test(produto): garantir 100% de cobertura JaCoCo`.

## 3. Conventional PRs
- **Título**: seguir exatamente o formato de Conventional Commit (`feat(escopo): descrição`).
- **Descrição**: incluir seções `## Resumo`, `## Alterações` e `## Verificação` (testes executados e validação de endpoints/migration).
