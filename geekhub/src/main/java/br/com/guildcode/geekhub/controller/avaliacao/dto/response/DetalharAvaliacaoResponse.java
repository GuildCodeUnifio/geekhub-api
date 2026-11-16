package br.com.guildcode.geekhub.controller.avaliacao.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DetalharAvaliacaoResponse(

        Long id,
        Long pedidoId,
        Long vendedorId,
        AutorAvaliacaoResponse autor,
        Integer nota,
        String comentario,
        LocalDateTime criadoEm
) {}
