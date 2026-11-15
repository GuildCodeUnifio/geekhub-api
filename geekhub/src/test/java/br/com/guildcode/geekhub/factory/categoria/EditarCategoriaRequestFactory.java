package br.com.guildcode.geekhub.factory.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class EditarCategoriaRequestFactory {

    public static EditarCategoriaRequest get() {

        return EditarCategoriaRequest.builder()
                .nome("Figuras colecionáveis")
                .descricao("Descrição atualizada de colecionáveis")
                .ativa(TRUE)
                .build();
    }

    public static EditarCategoriaRequest comNovoNome() {

        return EditarCategoriaRequest.builder()
                .nome("Board Games")
                .descricao("Jogos de tabuleiro e expansões")
                .ativa(FALSE)
                .build();
    }
}
