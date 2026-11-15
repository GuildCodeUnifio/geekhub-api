package br.com.guildcode.geekhub.controller.pedido.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EventoPedidoResponse(

        EstadoPedido estado,
        LocalDateTime ocorridoEm
) {}
