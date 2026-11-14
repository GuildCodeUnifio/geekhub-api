package br.com.guildcode.geekhub.controller.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LoginRequest(

        @NotNull(message = "é obrigatório")
        @Email(message = "deve ser válido")
        String email,

        @NotBlank(message = "é obrigatória")
        String senha
) {}
