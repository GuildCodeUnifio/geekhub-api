---
trigger: glob
globs: "**/postman/**/*.json, **/openapi/**/*.java"
---

# Regras de Documentação OpenAPI e Postman

1. **Interfaces `*ControllerOpenApi`**: ficam em `openapi/<entidade>/`, centralizando `@Tag`, `@Operation` e `@ApiResponses` (`200/201/204`, `400`, `401`, `403` quando restrito a `ADMIN`, `404`, `409`).
2. **Collection do Postman (`geekhub/postman/geekhub.postman_collection.json`)**:
   - **1 pasta por recurso** (ex: `usuarios`, `login`, `produtos`).
   - **1 request por endpoint/ação** (ex: `cadastrar`, `listar`, `detalhar`, `editar`, `remover`).
   - **Exatamente 3 exemplos salvos (`response`)** dentro de cada request (1 cenário de sucesso e 2 cenários de erro mais relevantes).
   - Endpoints protegidos usam Auth Bearer `{{token}}`.
   - Campos de URL de imagem usam `https://picsum.photos/200`.
   - Sempre validar a sintaxe JSON do arquivo após editar (`ConvertFrom-Json` ou `jq`).
