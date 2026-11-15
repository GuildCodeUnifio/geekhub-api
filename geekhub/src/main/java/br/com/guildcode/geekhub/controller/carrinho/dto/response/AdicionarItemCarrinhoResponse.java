package br.com.guildcode.geekhub.controller.carrinho.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AdicionarItemCarrinhoResponse(

        Long id,
        Long anuncioId,
        String nomeProduto,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal,
        LocalDateTime criadoEm
) {}
