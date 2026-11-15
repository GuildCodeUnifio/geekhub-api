package br.com.guildcode.geekhub.controller.carrinho.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DetalharItemCarrinhoResponse(

        Long id,
        Long anuncioId,
        Long vendedorId,
        String nomeProduto,
        String imagem,
        BigDecimal precoUnitario,
        Integer quantidade,
        Integer quantidadeDisponivel,
        Boolean disponivel,
        BigDecimal subtotal,
        LocalDateTime criadoEm
) {}
