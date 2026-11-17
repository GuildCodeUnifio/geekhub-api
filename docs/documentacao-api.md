# Documentação da API

Para visualizar a documentação completa dos endpoints (rotas, parâmetros, DTOs, autenticação JWT e códigos de resposta), inicie a aplicação e acesse o **Swagger**.

---

## Como Acessar o Swagger

1. Garanta que o **MySQL 8** esteja em execução (ou abra o projeto no **Dev Container**, que já sobe banco, Java 21 e Maven configurados).
2. Inicie a API dentro da pasta `geekhub/`:
   ```bash
   # Linux / macOS / Dev Container
   ./mvnw spring-boot:run

   # Windows
   .\mvnw.cmd spring-boot:run
   ```
3. Acesse no navegador após a inicialização:
   - **Swagger UI (interativo)**: `http://localhost:8080/swagger-ui.html`
   - **Especificação OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

> Para testar rotas protegidas no Swagger UI, autentique em `POST /login` com um usuário inicial (`admin@geekhub.com`, `usuario@geekhub.com` ou `vendedor@geekhub.com`, senha `123456`), copie o `accessToken` e informe no botão **Authorize**.

---

## Referências

- Instruções do projeto: [README.md](../README.md)
- Coleção Postman com exemplos salvos: [geekhub.postman_collection.json](../postman/geekhub.postman_collection.json)
