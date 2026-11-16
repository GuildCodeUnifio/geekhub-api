package br.com.guildcode.geekhub.controller.usuario.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AvaliacaoPerfilVendedorResponse(

        Long id,
        Long pedidoId,
        Long autorId,
        String autorNome,
        String autorImagemPerfil,
        Integer nota,
        String comentario,
        LocalDateTime criadoEm
) {}
