---
name: validar-entrega
description: Executa a bateria de validação completa da geekhub-api (mvnw test, cobertura 100% no JaCoCo, validação do JSON do Postman e revisão do checklist arquitetural). Use ao finalizar uma feature ou ao digitar /validar-entrega.
---

# Validar Entrega da Feature (`/validar-entrega`)

Execute este checklist completo antes de encerrar qualquer demanda na `geekhub-api`:

## 1. Testes e Cobertura JaCoCo (100%)
1. Dentro de `geekhub/`, execute:
   - Windows: `.\mvnw.cmd test`
   - Linux/Dev Container: `./mvnw test`
2. Confirme que todos os testes passaram (`Failures: 0, Errors: 0, Skipped: 0`).
3. Confirme que eventuais consultas personalizadas criadas com `@Query` (JPQL ou nativa) em `*Repository` possuem `*RepositoryTest` (`@DataJpaTest`, `@AutoConfigureTestDatabase(replace = NONE)` e `@ActiveProfiles("test")`) testando apenas esses métodos customizados.
4. Leia `geekhub/target/site/jacoco/jacoco.csv` e verifique que **todas** as classes possuem `0` nas colunas `INSTRUCTION_MISSED`, `BRANCH_MISSED`, `LINE_MISSED`, `COMPLEXITY_MISSED` e `METHOD_MISSED`.
   - Se houver qualquer linha/branch não coberta, complete os testes antes de concluir.

## 2. Validação da Collection do Postman
1. Valide o JSON de `postman/geekhub.postman_collection.json`:
   - PowerShell: `Get-Content -Raw postman/geekhub.postman_collection.json | ConvertFrom-Json | Out-Null`
   - Linux: `jq empty postman/geekhub.postman_collection.json`
2. Confira se todo novo endpoint está na pasta do seu recurso e possui **no mínimo 3 exemplos salvos (`response`)**.

## 3. Auditoria Arquitetural Rápida
Verifique nos arquivos modificados/criados:
- Nenhum `@Autowired` nos arquivos Java.
- Nenhum tipo primitivo (`boolean`, `int`, `long`, `double`) nas entidades, DTOs ou serviços.
- Nenhum `*Validator` ou `*Repository` injetado diretamente em `*Controller`.
- DTOs são `record` com `@Builder`; Controllers implementam `*ControllerOpenApi` sem `@Override` e possuem `@ResponseStatus`.
- Cálculos/derivações próprias de entidades estão como métodos na `@Entity` (sem duplicação em Mappers) e possuem `<Entidade>Test` unitário.
- Novas entidades/colunas possuem migration correspondente em `db/migration/`.

## 4. Saída Final
Ao concluir, execute `/git-flow` (ou sugira nome de branch, commit e PR no padrão do repositório) caso o usuário vá subir as alterações.
