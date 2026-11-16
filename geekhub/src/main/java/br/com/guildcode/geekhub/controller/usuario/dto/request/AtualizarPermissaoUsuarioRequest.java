package br.com.guildcode.geekhub.controller.usuario.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AtualizarPermissaoUsuarioRequest(

        @NotNull(message = "é obrigatório")
        Boolean admin
) {}
