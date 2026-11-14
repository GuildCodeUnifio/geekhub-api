---
name: nova-migracao
description: Cria uma nova migration versionada do Flyway (V<N>__<descricao>.sql) compatível com MySQL 8 e Hibernate ddl-auto=validate. Use quando precisar criar ou alterar tabelas ou adicionar dados iniciais (seed), ou ao digitar /nova-migracao.
---

# Criar Nova Migration Flyway (`/nova-migracao`)

A API utiliza **Flyway** com `spring.jpa.hibernate.ddl-auto=validate` e `FlywayConfig` (`clean` + `migrate` na inicialização).

## Passos

1. **Verifique a última versão existente**:
   - Liste `geekhub/src/main/resources/db/migration/` e identifique o maior número `V<N>` (ex: `V1__...` → próxima é `V2__...`).

2. **Crie o arquivo SQL**:
   - Caminho: `geekhub/src/main/resources/db/migration/V<N>__<descricao_em_snake_case>.sql`.
   - Use apenas SQL válido para **MySQL 8**:
     - IDs: `BIGINT NOT NULL AUTO_INCREMENT` + `PRIMARY KEY (id)`
     - Booleanos: `BOOLEAN NOT NULL DEFAULT TRUE`
     - Datas/horas (`LocalDateTime`): `DATETIME(6) NOT NULL`
     - Valores monetários (`BigDecimal`): `DECIMAL(10, 2) NOT NULL` (alinhe com `@Column(precision = 10, scale = 2)`)
     - Chaves estrangeiras nomeadas explicitamente (`CONSTRAINT fk_<origem>_<destino> FOREIGN KEY (...) REFERENCES ...`)

3. **Alinhe com as entidades JPA (`domain/`)**:
   - Confirme que todos os nomes de tabela, coluna, `nullable`, `unique` e `length` correspondem exatamente às anotações `@Entity`, `@Table`, `@Column` e `@JoinColumn`.

4. **Adicione seed inicial (se aplicável)**:
   - Insira dados de teste consistentes em português utilizando `https://picsum.photos/200` para imagens e o hash BCrypt `$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG` se criar usuários com senha `123456`.

5. **Valide contra o MySQL**:
   - Compile o projeto e suba a aplicação no Dev Container (ou rode os testes) garantindo que `Flyway` aplique a migration sem erro e que o Hibernate `ddl-auto=validate` valide o schema sem divergências.
