package br.com.guildcode.geekhub.factory.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;

public class CadastrarCategoriaRequestFactory {

    public static CadastrarCategoriaRequest get() {

        return CadastrarCategoriaRequest.builder()
                .nome("Figuras colecionáveis")
                .descricao("Action figures, estátuas e colecionáveis em escala")
                .build();
    }
}
