CREATE TABLE usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(128) NOT NULL,
    imagem_perfil VARCHAR(512),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE permissao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_permissao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

-- Senha padrão para ambos os usuários: 123456 (gerada com BCrypt)
INSERT INTO usuario (nome, email, senha, imagem_perfil, ativo, criado_em)
VALUES ('Administrador GeekHub', 'admin@geekhub.com', '$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG', 'https://picsum.photos/200', TRUE, '2026-11-13 18:00:00.000000');

INSERT INTO usuario (nome, email, senha, imagem_perfil, ativo, criado_em)
VALUES ('Usuario GeekHub', 'usuario@geekhub.com', '$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG', 'https://picsum.photos/200', TRUE, '2026-11-13 18:00:00.000000');

-- Permissões do Administrador (id = 1)
INSERT INTO permissao (nome, usuario_id) VALUES ('ADMIN', 1);
INSERT INTO permissao (nome, usuario_id) VALUES ('USUARIO', 1);

-- Permissão do Usuário Comum (id = 2)
INSERT INTO permissao (nome, usuario_id) VALUES ('USUARIO', 2);
