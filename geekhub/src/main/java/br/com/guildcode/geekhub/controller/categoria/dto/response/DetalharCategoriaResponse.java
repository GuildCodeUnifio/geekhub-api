package br.com.guildcode.geekhub.controller.categoria.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DetalharCategoriaResponse(

        Long id,
        String nome,
        String descricao,
        Boolean ativa,
        LocalDateTime criadoEm
) {}
