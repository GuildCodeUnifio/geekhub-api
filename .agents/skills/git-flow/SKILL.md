---
name: git-flow
description: Analisa as alterações atuais do repositório e sugere o nome da branch, mensagem de Conventional Commit e título/corpo de Conventional PR seguindo o padrão do projeto. Use quando o usuário pedir commit, PR ou nome de branch, ou ao digitar /git-flow.
---

# Gerar Padrão de Branch, Commit e PR (`/git-flow`)

Inspecione `git -C geekhub-api status -s` e `git -C geekhub-api diff --stat` (sem executar `git commit` ou `git push`, a menos que o usuário peça explicitamente) e gere as sugestões abaixo em português:

## 1. Sugestão de Branch
Formato: `<tipo>/<identificador-opcional>-<descricao-curta>`
- Regras: apenas minúsculas (`lowercase`), hífens (`-`), sem espaços, acentos ou sublinhados (`_`).
- Tipos: `feat/`, `fix/`, `hotfix/`, `chore/`, `docs/`, `refactor/`, `test/`, `release/`.

## 2. Sugestão de Commit (Conventional Commits)
Formato:
```text
<tipo>(<escopo>): <descrição curta no imperativo em minúsculas sem ponto final>
```
- Tipos válidos: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `style`, `ci`.

## 3. Sugestão de Pull Request (Conventional PR)
- **Título**: idêntico ao padrão do Conventional Commit.
- **Corpo**:
  ```markdown
  ## Resumo
  - <resumo objetivo do que foi implementado>

  ## Alterações
  - <principais camadas/endpoints/migrations alterados>

  ## Verificação
  - [ ] `mvnw test` (100% de cobertura JaCoCo)
  - [ ] Collection do Postman atualizada e validada
  - [ ] Migration Flyway / MySQL validada
  ```
