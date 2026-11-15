package br.com.guildcode.geekhub.factory.categoria;

import br.com.guildcode.geekhub.domain.categoria.Categoria;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

public class CategoriaFactory {

    public static Categoria get() {

        return Categoria.builder()
                .id(1L)
                .nome("Figuras colecionáveis")
                .descricao("Action figures, estátuas e colecionáveis em escala")
                .ativa(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
    }
}
