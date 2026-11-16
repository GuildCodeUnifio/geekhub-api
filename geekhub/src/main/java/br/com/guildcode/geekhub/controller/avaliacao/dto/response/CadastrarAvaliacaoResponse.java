package br.com.guildcode.geekhub.controller.avaliacao.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CadastrarAvaliacaoResponse(

        Long id,
        Long pedidoId,
        Long vendedorId,
        Long autorId,
        String autorNome,
        Integer nota,
        String comentario,
        LocalDateTime criadoEm
) {}
