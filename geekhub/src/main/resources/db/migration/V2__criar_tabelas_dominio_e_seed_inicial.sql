CREATE TABLE categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE anuncio (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(2000) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    quantidade_disponivel INT NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    condicao VARCHAR(30) NOT NULL,
    conservacao VARCHAR(30) NOT NULL,
    categoria_id BIGINT NOT NULL,
    vendedor_id BIGINT NOT NULL,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_anuncio_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT fk_anuncio_vendedor FOREIGN KEY (vendedor_id) REFERENCES usuario (id)
);

CREATE TABLE anuncio_imagem (
    anuncio_id BIGINT NOT NULL,
    url_imagem VARCHAR(512) NOT NULL,
    ordem INT NOT NULL,
    PRIMARY KEY (anuncio_id, ordem),
    CONSTRAINT fk_anuncio_imagem_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncio (id) ON DELETE CASCADE
);

CREATE TABLE item_carrinho (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comprador_id BIGINT NOT NULL,
    anuncio_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_item_carrinho_comprador_anuncio UNIQUE (comprador_id, anuncio_id),
    CONSTRAINT fk_item_carrinho_comprador FOREIGN KEY (comprador_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_carrinho_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncio (id) ON DELETE CASCADE
);

CREATE TABLE pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comprador_id BIGINT NOT NULL,
    comprador_nome VARCHAR(255) NOT NULL,
    comprador_email VARCHAR(255) NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    avaliado BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pedido_comprador FOREIGN KEY (comprador_id) REFERENCES usuario (id)
);

CREATE TABLE pedido_item (
    pedido_id BIGINT NOT NULL,
    anuncio_id BIGINT NOT NULL,
    vendedor_id BIGINT NOT NULL,
    nome_produto VARCHAR(255) NOT NULL,
    imagem_produto VARCHAR(512),
    preco_unitario DECIMAL(10, 2) NOT NULL,
    quantidade INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_pedido_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE,
    CONSTRAINT fk_pedido_item_anuncio FOREIGN KEY (anuncio_id) REFERENCES anuncio (id),
    CONSTRAINT fk_pedido_item_vendedor FOREIGN KEY (vendedor_id) REFERENCES usuario (id)
);

CREATE TABLE pedido_evento (
    pedido_id BIGINT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    ocorrido_em DATETIME(6) NOT NULL,
    ordem INT NOT NULL,
    PRIMARY KEY (pedido_id, ordem),
    CONSTRAINT fk_pedido_evento_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id) ON DELETE CASCADE
);

CREATE TABLE avaliacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pedido_id BIGINT NOT NULL UNIQUE,
    autor_id BIGINT NOT NULL,
    vendedor_id BIGINT NOT NULL,
    nota INT NOT NULL,
    comentario VARCHAR(1000),
    criado_em DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_avaliacao_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_avaliacao_autor FOREIGN KEY (autor_id) REFERENCES usuario (id),
    CONSTRAINT fk_avaliacao_vendedor FOREIGN KEY (vendedor_id) REFERENCES usuario (id)
);

-- Usuário adicional de teste (id = 3, senha: 123456)
INSERT INTO usuario (nome, email, senha, imagem_perfil, ativo, criado_em)
VALUES ('Vendedor GeekHub', 'vendedor@geekhub.com', '$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG', 'https://picsum.photos/200', TRUE, '2026-11-13 18:00:00.000000');

INSERT INTO permissao (nome, usuario_id) VALUES ('USUARIO', 3);

-- Categorias iniciais (ativas e 1 desativada)
INSERT INTO categoria (nome, descricao, ativa, criado_em)
VALUES ('Figuras colecionáveis', 'Action figures, estátuas e colecionáveis em escala', TRUE, '2026-11-13 18:05:00.000000');

INSERT INTO categoria (nome, descricao, ativa, criado_em)
VALUES ('Quadrinhos e mangás', 'HQs, mangás e edições especiais de colecionador', TRUE, '2026-11-13 18:05:00.000000');

INSERT INTO categoria (nome, descricao, ativa, criado_em)
VALUES ('Jogos', 'Jogos físicos, consoles retrô e itens de RPG', TRUE, '2026-11-13 18:05:00.000000');

INSERT INTO categoria (nome, descricao, ativa, criado_em)
VALUES ('Acessórios', 'Pôsteres, periféricos e itens decorativos geek', TRUE, '2026-11-13 18:05:00.000000');

INSERT INTO categoria (nome, descricao, ativa, criado_em)
VALUES ('Cartas Colecionáveis', 'Categoria desativada para testes de validação de negócio', FALSE, '2026-11-13 18:05:00.000000');

-- Anúncios iniciais em diferentes condições, conservações e estoques
INSERT INTO anuncio (nome, descricao, preco, quantidade_disponivel, disponivel, condicao, conservacao, categoria_id, vendedor_id, criado_em)
VALUES ('Action figure Naruto 1ª edição', 'Peça original de colecionador lacrada na caixa com nota fiscal.', 1850.00, 3, TRUE, 'NOVO', 'EXCELENTE', 1, 3, '2026-11-13 18:10:00.000000');

INSERT INTO anuncio (nome, descricao, preco, quantidade_disponivel, disponivel, condicao, conservacao, categoria_id, vendedor_id, criado_em)
VALUES ('Mangá Colecionável — Edição Especial', 'Volume de colecionador com sobrecapa e páginas coloridas.', 49.90, 5, TRUE, 'NOVO', 'EXCELENTE', 2, 3, '2026-11-13 18:11:00.000000');

INSERT INTO anuncio (nome, descricao, preco, quantidade_disponivel, disponivel, condicao, conservacao, categoria_id, vendedor_id, criado_em)
VALUES ('Estátua Kakashi Hatake 18cm', 'Peça em PVC exposta apenas em cristaleira, acompanha caixa original.', 329.90, 2, TRUE, 'USADO', 'BOM', 1, 3, '2026-11-13 18:12:00.000000');

INSERT INTO anuncio (nome, descricao, preco, quantidade_disponivel, disponivel, condicao, conservacao, categoria_id, vendedor_id, criado_em)
VALUES ('HQ Batman: O Cavaleiro das Trevas', 'Edição capa dura com marcas leves de manuseio na lombada.', 139.90, 4, TRUE, 'USADO', 'REGULAR', 2, 2, '2026-11-13 18:13:00.000000');

INSERT INTO anuncio (nome, descricao, preco, quantidade_disponivel, disponivel, condicao, conservacao, categoria_id, vendedor_id, criado_em)
VALUES ('Miniatura Mecha Edição Esgotada', 'Item indisponível para validação de filtragem de catálogo e estoque.', 490.00, 0, FALSE, 'NOVO', 'EXCELENTE', 1, 3, '2026-11-13 18:14:00.000000');

-- Imagens dos anúncios
INSERT INTO anuncio_imagem (anuncio_id, url_imagem, ordem) VALUES (1, 'https://picsum.photos/200', 0);
INSERT INTO anuncio_imagem (anuncio_id, url_imagem, ordem) VALUES (1, 'https://picsum.photos/200', 1);
INSERT INTO anuncio_imagem (anuncio_id, url_imagem, ordem) VALUES (2, 'https://picsum.photos/200', 0);
INSERT INTO anuncio_imagem (anuncio_id, url_imagem, ordem) VALUES (3, 'https://picsum.photos/200', 0);
INSERT INTO anuncio_imagem (anuncio_id, url_imagem, ordem) VALUES (4, 'https://picsum.photos/200', 0);
INSERT INTO anuncio_imagem (anuncio_id, url_imagem, ordem) VALUES (5, 'https://picsum.photos/200', 0);

-- Item inicial no carrinho do usuário comum (id = 2)
INSERT INTO item_carrinho (comprador_id, anuncio_id, quantidade, criado_em)
VALUES (2, 2, 2, '2026-11-13 18:20:00.000000');

-- Pedidos iniciais em estados CRIADO, ENVIADO e CONCLUIDO
INSERT INTO pedido (comprador_id, comprador_nome, comprador_email, valor_total, estado, avaliado, criado_em)
VALUES (2, 'Usuario GeekHub', 'usuario@geekhub.com', 1850.00, 'CRIADO', FALSE, '2026-11-13 18:30:00.000000');

INSERT INTO pedido (comprador_id, comprador_nome, comprador_email, valor_total, estado, avaliado, criado_em)
VALUES (2, 'Usuario GeekHub', 'usuario@geekhub.com', 329.90, 'ENVIADO', FALSE, '2026-11-13 18:35:00.000000');

INSERT INTO pedido (comprador_id, comprador_nome, comprador_email, valor_total, estado, avaliado, criado_em)
VALUES (2, 'Usuario GeekHub', 'usuario@geekhub.com', 99.80, 'CONCLUIDO', FALSE, '2026-11-13 18:40:00.000000');

INSERT INTO pedido (comprador_id, comprador_nome, comprador_email, valor_total, estado, avaliado, criado_em)
VALUES (2, 'Usuario GeekHub', 'usuario@geekhub.com', 49.90, 'CONCLUIDO', TRUE, '2026-11-13 18:45:00.000000');

-- Itens congelados dos pedidos
INSERT INTO pedido_item (pedido_id, anuncio_id, vendedor_id, nome_produto, imagem_produto, preco_unitario, quantidade, subtotal)
VALUES (1, 1, 3, 'Action figure Naruto 1ª edição', 'https://picsum.photos/200', 1850.00, 1, 1850.00);

INSERT INTO pedido_item (pedido_id, anuncio_id, vendedor_id, nome_produto, imagem_produto, preco_unitario, quantidade, subtotal)
VALUES (2, 3, 3, 'Estátua Kakashi Hatake 18cm', 'https://picsum.photos/200', 329.90, 1, 329.90);

INSERT INTO pedido_item (pedido_id, anuncio_id, vendedor_id, nome_produto, imagem_produto, preco_unitario, quantidade, subtotal)
VALUES (3, 2, 3, 'Mangá Colecionável — Edição Especial', 'https://picsum.photos/200', 49.90, 2, 99.80);

INSERT INTO pedido_item (pedido_id, anuncio_id, vendedor_id, nome_produto, imagem_produto, preco_unitario, quantidade, subtotal)
VALUES (4, 2, 3, 'Mangá Colecionável — Edição Especial', 'https://picsum.photos/200', 49.90, 1, 49.90);

-- Histórico de eventos dos pedidos
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (1, 'CRIADO', '2026-11-13 18:30:00.000000', 0);

INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (2, 'CRIADO', '2026-11-13 18:35:00.000000', 0);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (2, 'CONFIRMADO', '2026-11-13 18:36:00.000000', 1);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (2, 'ENVIADO', '2026-11-13 18:37:00.000000', 2);

INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (3, 'CRIADO', '2026-11-13 18:40:00.000000', 0);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (3, 'CONFIRMADO', '2026-11-13 18:41:00.000000', 1);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (3, 'ENVIADO', '2026-11-13 18:42:00.000000', 2);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (3, 'CONCLUIDO', '2026-11-13 18:43:00.000000', 3);

INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (4, 'CRIADO', '2026-11-13 18:45:00.000000', 0);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (4, 'CONFIRMADO', '2026-11-13 18:46:00.000000', 1);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (4, 'ENVIADO', '2026-11-13 18:47:00.000000', 2);
INSERT INTO pedido_evento (pedido_id, estado, ocorrido_em, ordem) VALUES (4, 'CONCLUIDO', '2026-11-13 18:48:00.000000', 3);

-- Avaliação inicial de exemplo para o pedido 4
INSERT INTO avaliacao (pedido_id, autor_id, vendedor_id, nota, comentario, criado_em)
VALUES (4, 2, 3, 5, 'Produto muito bem embalado e entrega rápida!', '2026-11-13 18:50:00.000000');
