package br.com.guildcode.geekhub.controller.carrinho.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemCarrinhoDetalheResponse(

        Long id,
        Long anuncioId,
        Long vendedorId,
        String nome,
        String imagem,
        BigDecimal precoUnitario,
        Integer quantidade,
        Integer quantidadeDisponivel,
        Boolean disponivel,
        BigDecimal subtotal
) {}
