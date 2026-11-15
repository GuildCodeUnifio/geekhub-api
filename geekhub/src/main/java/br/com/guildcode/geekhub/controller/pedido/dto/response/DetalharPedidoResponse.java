package br.com.guildcode.geekhub.controller.pedido.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DetalharPedidoResponse(

        Long id,
        Long compradorId,
        String compradorNome,
        String compradorEmail,
        List<ItemPedidoResponse> itens,
        BigDecimal valorTotal,
        EstadoPedido estado,
        List<EventoPedidoResponse> historico,
        Boolean avaliado,
        LocalDateTime criadoEm
) {}
