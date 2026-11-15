package br.com.guildcode.geekhub.controller.pedido.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemPedidoResponse(

        Long anuncioId,
        Long vendedorId,
        String nomeProduto,
        String imagemProduto,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal
) {}
