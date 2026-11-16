package br.com.guildcode.geekhub.factory.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;

public class CadastrarAvaliacaoRequestFactory {

    public static CadastrarAvaliacaoRequest get() {

        return CadastrarAvaliacaoRequest.builder()
                .pedidoId(3L)
                .nota(5)
                .comentario("Produto muito bem embalado e entrega rápida!")
                .build();
    }
}
