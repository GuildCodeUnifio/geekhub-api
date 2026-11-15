package br.com.guildcode.geekhub.controller.carrinho.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ConsultarCarrinhoResponse(

        Long compradorId,
        List<ItemCarrinhoDetalheResponse> itens,
        BigDecimal total
) {}
