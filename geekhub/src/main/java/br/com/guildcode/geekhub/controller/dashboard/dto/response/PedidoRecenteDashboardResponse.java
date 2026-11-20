package br.com.guildcode.geekhub.controller.dashboard.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PedidoRecenteDashboardResponse(

        Long id,
        String compradorNome,
        EstadoPedido estado,
        BigDecimal valorTotal,
        Integer quantidadeItens,
        LocalDateTime criadoEm
) {}
