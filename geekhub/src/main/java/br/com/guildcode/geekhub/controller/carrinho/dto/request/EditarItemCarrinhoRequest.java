package br.com.guildcode.geekhub.controller.carrinho.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EditarItemCarrinhoRequest(

        @NotNull(message = "é obrigatória")
        @Min(value = 1, message = "deve ser no mínimo 1")
        Integer quantidade
) {}
