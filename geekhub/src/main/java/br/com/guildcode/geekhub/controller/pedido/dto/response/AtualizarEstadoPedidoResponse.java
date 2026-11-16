package br.com.guildcode.geekhub.controller.pedido.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

import java.util.List;

@Builder
public record AtualizarEstadoPedidoResponse(

        Long id,
        EstadoPedido estado,
        List<EventoPedidoResponse> historico,
        Boolean avaliado
) {}
