package br.com.guildcode.geekhub.factory.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;

public class EditarAvaliacaoRequestFactory {

    public static EditarAvaliacaoRequest get() {

        return EditarAvaliacaoRequest.builder()
                .nota(4)
                .comentario("Comentário atualizado após revisão.")
                .build();
    }
}
