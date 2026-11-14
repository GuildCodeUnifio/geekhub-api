# GeekHub API

> Projeto em desenvolvimento — Projeto Integrador UNIFIO (Grupo **GuildCode**).

API REST do marketplace **GeekHub**, construída com Java 21, Spring Boot 4, Spring Security (JWT RSA), MySQL 8 e Flyway.

---

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.2 |
| Segurança | Spring Security + OAuth2 Resource Server (JWT RSA) |
| Banco de dados | MySQL 8 + Flyway |
| Documentação | SpringDoc OpenAPI (Swagger) |
| Testes e Cobertura | JUnit 5 + Mockito + JaCoCo (`100%`) |

---

## Como Executar

### Opção 1: Dev Container (Recomendado)
1. Abra a pasta `geekhub-api` no VS Code com a extensão **Dev Containers**.
2. Selecione **Reopen in Container** (Java 21, Maven e MySQL 8 já sobem configurados).
3. Dentro de `geekhub/`, execute:
   ```bash
   ./mvnw spring-boot:run
   ```

### Opção 2: Local (Windows / Linux)
Certifique-se de ter **Java 21** e **MySQL 8** rodando na porta `3306` com as credenciais configuradas em `geekhub/src/main/resources/application.properties`, e execute dentro de `geekhub/`:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

> **Observação:** Em ambiente de desenvolvimento, o `FlywayConfig` limpa e reaplica a migration inicial (`V1`) a cada inicialização, garantindo o banco sempre no estado padrão.

---

## Usuários Iniciais (Flyway `V1`)

| Perfil | E-mail | Senha | Permissões |
| --- | --- | --- | --- |
| Administrador | `admin@geekhub.com` | `123456` | `ADMIN`, `USUARIO` |
| Usuário Comum | `usuario@geekhub.com` | `123456` | `USUARIO` |

---

## Documentação e Testes Manuais

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Postman Collection**: [geekhub.postman_collection.json](geekhub/postman/geekhub.postman_collection.json)

---

## Endpoints Disponíveis (em construção)

| Método | Rota | Acesso | Descrição |
| --- | --- | --- | --- |
| `POST` | `/login` | Público | Autentica usuário e retorna token JWT |
| `POST` | `/usuarios` | Público | Cadastra novo usuário (`USUARIO`) |
| `GET` | `/usuarios` | `ADMIN` | Lista usuários de forma paginada |
| `GET` | `/usuarios/{id}` | Autenticado | Detalha usuário por ID |
| `PUT` | `/usuarios/{id}` | Autenticado | Atualiza dados do usuário |
| `DELETE` | `/usuarios/{id}` | `ADMIN` | Remove usuário por ID |

---

## Executando os Testes Unitários

```bash
# Dentro da pasta geekhub/
.\mvnw.cmd test   # Windows
./mvnw test       # Linux / Dev Container
```

O relatório de cobertura JaCoCo é gerado em `geekhub/target/site/jacoco/jacoco.csv`.

---

## Contribuição e Arquitetura

- Guia de contribuição e fluxo Git: [CONTRIBUTING.md](CONTRIBUTING.md)
- Regras oficiais da arquitetura: [AGENTS.md](AGENTS.md)

## Licença

Este projeto está licenciado sob a **GNU General Public License v3.0 (GPL-3.0)** — veja o arquivo [LICENSE](LICENSE) para mais detalhes.
