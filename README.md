# GeekHub API

API REST do Projeto Integrador **GeekHub** (Grupo **GuildCode** — UNIFIO), um marketplace para compra e venda de produtos geek e colecionáveis.

O backend gerencia cadastro e autenticação de usuários com JWT RSA, catálogo de categorias e anúncios, carrinho de compras, ciclo de pedidos com controle e estorno de estoque, avaliações pós-compra e perfil público consolidado do vendedor.

---

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.2 |
| Segurança | Spring Security + OAuth2 Resource Server (JWT RSA) |
| Persistência | Spring Data JPA + Hibernate (`ddl-auto=validate`) |
| Banco de Dados e Migrations | MySQL 8 + Flyway |
| Documentação da API | SpringDoc OpenAPI (`3.2.0`) / Swagger UI |
| Testes e Cobertura | JUnit 5 + Mockito + H2 (testes) + JaCoCo `0.8.12` (`100%`) |

---

## Como Executar

### Opção 1: Dev Container / GitHub Codespaces (Recomendado)

1. Abra a pasta `geekhub-api` no VS Code com a extensão **Dev Containers** (ou inicie um GitHub Codespace).
2. Selecione **Reopen in Container**. O ambiente instala Java 21, Maven e MySQL 8 e executa `.devcontainer/init-mysql.sh` para criar o banco `ecommerce` e o usuário `geekhub`.
3. Entre na pasta `geekhub/` e inicie a aplicação:
   ```bash
   cd geekhub
   ./mvnw spring-boot:run
   ```

### Opção 2: Execução Local (Windows / Linux)

1. Instale **Java 21** e **MySQL 8** em execução na porta `3306`.
2. Crie o banco `ecommerce` e o usuário `geekhub` (senha `geekhub`), conforme configurado em `geekhub/src/main/resources/application.properties`.
3. Entre na pasta `geekhub/` e execute a aplicação:
   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run

   # Linux / macOS
   ./mvnw spring-boot:run
   ```

A API inicia em `http://localhost:8080`. Em desenvolvimento, o `FlywayConfig` limpa o banco e reaplica as migrations `V1` e `V2` a cada inicialização.

### Opção 3: Deploy em Nuvem (Render Free)

O repositório inclui [Dockerfile](Dockerfile) e [render.yaml](render.yaml) configurados com o profile `render` (`geekhub/src/main/resources/application-render.properties`), que sobe a aplicação com banco H2 em memória no modo MySQL (`MODE=MySQL`) e executa automaticamente as migrations `V1` e `V2` do Flyway a cada inicialização. Basta criar um **Blueprint** ou **Web Service (Docker)** no Render apontando para este repositório.

---

## Usuários Iniciais (Migrations `V1` e `V2`)

| Perfil | E-mail | Senha | Permissões |
| --- | --- | --- | --- |
| Administrador | `admin@geekhub.com` | `123456` | `ADMIN`, `USUARIO` |
| Usuário Comum | `usuario@geekhub.com` | `123456` | `USUARIO` |
| Vendedor (`V2`) | `vendedor@geekhub.com` | `123456` | `USUARIO` |

---

## Documentação e Coleção Postman

- **Documentação da API (Swagger e endpoints)**: [docs/documentacao-api.md](docs/documentacao-api.md)
- **Diagrama da Arquitetura em Camadas**: [docs/arquitetura-camadas.svg](docs/arquitetura-camadas.svg)
- **Diagrama do Banco de Dados (DER)**: [docs/diagrama-db.svg](docs/diagrama-db.svg)
- **Diagramas de Sequência (Cadastro e Login)**: [docs/diagramas-sequencia-auth.md](docs/diagramas-sequencia-auth.md)
- **Coleção Postman (com variáveis e respostas salvas)**: [postman/geekhub.postman_collection.json](postman/geekhub.postman_collection.json)

---

## Endpoints Principais

Para visualizar a documentação completa (parâmetros, DTOs, autenticação JWT e códigos de resposta), inicie a API e acesse o Swagger (detalhes em [docs/documentacao-api.md](docs/documentacao-api.md)):

- **Swagger UI (interativo)**: `http://localhost:8080/swagger-ui.html`
- **Contrato OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

Recursos disponíveis: `/login`, `/usuarios`, `/categorias`, `/anuncios`, `/carrinho`, `/pedidos`, `/avaliacoes` e `/dashboard` (`ADMIN`).

---

## Testes e Cobertura JaCoCo

Os testes usam banco H2 em memória (`geekhub/src/test/resources/application-test.properties`). Execute dentro da pasta `geekhub/`:

```bash
# Windows
.\mvnw.cmd test

# Linux / Dev Container
./mvnw test
```

O build exige `100%` de cobertura de instruções, branches, linhas e métodos para as camadas de regra de negócio e conversão (`service`, `validator`, `mapper` e `GlobalExceptionHandler`). O `pom.xml` exclui da métrica apenas classes estruturais e de framework (`GeekhubApplication`, `config`, `openapi`, `repository`, `domain`, `controller`/DTOs e `*Exception`), embora `controller`, `domain` e `repository` também possuam testes dedicados.

Após executar os testes, o relatório completo é gerado localmente em `geekhub/target/site/jacoco/index.html` e `geekhub/target/site/jacoco/jacoco.csv`.

---

## Integrantes (Grupo GuildCode)

| Nome Completo | RA | GitHub |
| --- | --- | --- |
| Bryan Alvarenga | `272426` | [@bryan-coursework](https://github.com/bryan-coursework) |
| Igor Bassetto Menon | `274582` | [@Igor-menon](https://github.com/Igor-menon) |
| Juan Pedrozo | `273889` | [@juanpedrozo](https://github.com/juanpedrozo) |
| Kauã Gustavo da Fonseca Rodrigues | `273722` | [@uaikauaa](https://github.com/uaikauaa) |
| Rafael Dias Garcia | `272207` | [@diasgarcia](https://github.com/diasgarcia) |
| Vitor Yago Ferreira dos Santos | `272261` | [@Cuidoka](https://github.com/Cuidoka) |

---

## Contribuição e Licença

- Guia de contribuição e fluxo Git: [CONTRIBUTING.md](CONTRIBUTING.md)
- Regras arquiteturais do projeto: [AGENTS.md](AGENTS.md)
- Licença: [GNU General Public License v3.0 (GPL-3.0)](LICENSE)
