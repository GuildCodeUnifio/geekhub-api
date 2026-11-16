package br.com.guildcode.geekhub.controller.usuario.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AtualizarPermissaoUsuarioResponse(

        Long id,
        String nome,
        String email,
        Boolean admin,
        List<String> permissoes
) {}
