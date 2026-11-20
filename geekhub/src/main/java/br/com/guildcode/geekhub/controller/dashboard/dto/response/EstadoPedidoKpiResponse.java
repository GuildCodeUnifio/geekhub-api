package br.com.guildcode.geekhub.controller.dashboard.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

@Builder
public record EstadoPedidoKpiResponse(

        EstadoPedido estado,
        Long quantidade,
        Integer percentual
) {}
