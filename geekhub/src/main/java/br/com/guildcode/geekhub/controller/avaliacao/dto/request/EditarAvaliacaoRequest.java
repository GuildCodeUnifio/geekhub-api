package br.com.guildcode.geekhub.controller.avaliacao.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EditarAvaliacaoRequest(

        @NotNull(message = "é obrigatória")
        @Min(value = 1, message = "deve ser no mínimo 1")
        @Max(value = 5, message = "deve ser no máximo 5")
        Integer nota,

        @Size(max = 1000, message = "deve ter no máximo 1000 caracteres")
        String comentario
) {}
