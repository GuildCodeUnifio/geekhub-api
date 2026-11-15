package br.com.guildcode.geekhub.factory.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;

public class EditarItemCarrinhoRequestFactory {

    public static EditarItemCarrinhoRequest get() {
        return EditarItemCarrinhoRequest.builder()
                .quantidade(3)
                .build();
    }
}
