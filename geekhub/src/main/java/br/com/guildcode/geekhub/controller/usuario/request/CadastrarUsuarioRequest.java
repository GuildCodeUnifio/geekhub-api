package br.com.guildcode.geekhub.controller.usuario.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CadastrarUsuarioRequest(

        @NotBlank(message = "é obrigatório")
        @Size(max = 255)
        String nome,

        @NotNull(message = "é obrigatório")
        @NotBlank(message = "é obrigatório")
        @Email(message = "deve ser válido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "é obrigatória")
        @Size(max = 128)
        String senha,

        @Size(max = 512)
        String imagemPerfil
) {}
