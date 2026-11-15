package br.com.guildcode.geekhub.controller.usuario.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ListarUsuarioResponse(

        Long id,
        String nome,
        String email,
        String imagemPerfil,
        Boolean ativo,
        LocalDateTime criadoEm
) {}
