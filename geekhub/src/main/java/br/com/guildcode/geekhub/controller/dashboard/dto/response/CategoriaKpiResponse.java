package br.com.guildcode.geekhub.controller.dashboard.dto.response;

import lombok.Builder;

@Builder
public record CategoriaKpiResponse(

        String nome,
        Long quantidadeAnuncios,
        Long estoqueTotal,
        Integer percentual
) {}
