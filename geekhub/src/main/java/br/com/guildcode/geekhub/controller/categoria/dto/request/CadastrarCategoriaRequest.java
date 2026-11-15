package br.com.guildcode.geekhub.controller.categoria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CadastrarCategoriaRequest(

        @NotBlank(message = "é obrigatório")
        @Size(max = 100)
        String nome,

        @Size(max = 255)
        String descricao
) {}
