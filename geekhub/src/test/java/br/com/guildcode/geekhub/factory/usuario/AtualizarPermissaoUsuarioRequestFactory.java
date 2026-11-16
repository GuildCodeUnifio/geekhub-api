package br.com.guildcode.geekhub.factory.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class AtualizarPermissaoUsuarioRequestFactory {

    public static AtualizarPermissaoUsuarioRequest promoverAdmin() {

        return AtualizarPermissaoUsuarioRequest.builder()
                .admin(TRUE)
                .build();
    }

    public static AtualizarPermissaoUsuarioRequest rebaixarAdmin() {

        return AtualizarPermissaoUsuarioRequest.builder()
                .admin(FALSE)
                .build();
    }
}
