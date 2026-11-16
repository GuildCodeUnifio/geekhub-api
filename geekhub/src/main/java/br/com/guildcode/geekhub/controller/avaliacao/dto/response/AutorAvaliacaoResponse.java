package br.com.guildcode.geekhub.controller.avaliacao.dto.response;

import lombok.Builder;

@Builder
public record AutorAvaliacaoResponse(

        Long id,
        String nome,
        String imagemPerfil
) {}
