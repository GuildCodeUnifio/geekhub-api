package br.com.guildcode.geekhub.controller.carrinho.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record EditarItemCarrinhoResponse(

        Long id,
        Long anuncioId,
        String nomeProduto,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal
) {}
