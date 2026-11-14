package br.com.guildcode.geekhub.controller.usuario.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CadastrarUsuarioResponse(

        Long id,
        String nome,
        String email,
        String imagemPerfil,
        LocalDateTime criadoEm
) {}
