package br.com.guildcode.geekhub.controller.carrinho.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record AdicionarItemCarrinhoRequest(

        @NotNull(message = "é obrigatório")
        @Positive(message = "deve ser positivo")
        Long anuncioId,

        @NotNull(message = "é obrigatória")
        @Min(value = 1, message = "deve ser no mínimo 1")
        Integer quantidade
) {}
