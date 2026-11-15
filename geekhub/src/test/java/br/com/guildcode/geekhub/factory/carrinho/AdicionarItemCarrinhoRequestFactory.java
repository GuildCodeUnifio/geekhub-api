package br.com.guildcode.geekhub.factory.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;

public class AdicionarItemCarrinhoRequestFactory {

    public static AdicionarItemCarrinhoRequest get() {
        return AdicionarItemCarrinhoRequest.builder()
                .anuncioId(1L)
                .quantidade(2)
                .build();
    }
}
