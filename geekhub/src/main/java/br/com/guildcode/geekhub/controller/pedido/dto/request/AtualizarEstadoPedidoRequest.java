package br.com.guildcode.geekhub.controller.pedido.dto.request;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AtualizarEstadoPedidoRequest(

        @NotNull(message = "é obrigatório")
        EstadoPedido estado
) {}
