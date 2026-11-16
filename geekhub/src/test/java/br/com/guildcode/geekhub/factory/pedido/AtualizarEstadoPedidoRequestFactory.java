package br.com.guildcode.geekhub.factory.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;

public class AtualizarEstadoPedidoRequestFactory {

    public static AtualizarEstadoPedidoRequest get() {

        return get(CONFIRMADO);
    }

    public static AtualizarEstadoPedidoRequest get(EstadoPedido estado) {

        return AtualizarEstadoPedidoRequest.builder()
                .estado(estado)
                .build();
    }
}
