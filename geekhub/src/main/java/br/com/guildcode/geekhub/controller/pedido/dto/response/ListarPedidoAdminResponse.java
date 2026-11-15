package br.com.guildcode.geekhub.controller.pedido.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ListarPedidoAdminResponse(

        Long id,
        Long compradorId,
        String compradorNome,
        String compradorEmail,
        BigDecimal valorTotal,
        EstadoPedido estado,
        Boolean avaliado,
        Integer quantidadeItens,
        LocalDateTime criadoEm
) {}
