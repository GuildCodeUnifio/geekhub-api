package br.com.guildcode.geekhub.factory.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;

public class CadastrarUsuarioRequestFactory {

    public static CadastrarUsuarioRequest get() {

        return CadastrarUsuarioRequest.builder()
                .nome("Rafael Garcia")
                .email("rafael@email.com")
                .senha("123456")
                .imagemPerfil("http://foto.jpg")
                .build();
    }
}
