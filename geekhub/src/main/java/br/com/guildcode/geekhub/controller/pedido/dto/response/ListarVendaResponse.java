package br.com.guildcode.geekhub.controller.pedido.dto.response;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ListarVendaResponse(

        Long id,
        String compradorNome,
        BigDecimal valorTotal,
        EstadoPedido estado,
        LocalDateTime criadoEm
) {}
