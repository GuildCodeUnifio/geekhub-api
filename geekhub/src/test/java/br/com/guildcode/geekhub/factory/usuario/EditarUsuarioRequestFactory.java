package br.com.guildcode.geekhub.factory.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.EditarUsuarioRequest;

public class EditarUsuarioRequestFactory {

    public static EditarUsuarioRequest get() {

        return EditarUsuarioRequest.builder()
                .nome("Rafael Atualizado")
                .email("rafael@email.com")
                .imagemPerfil("https://picsum.photos/200")
                .build();
    }

    public static EditarUsuarioRequest comNovoEmail() {

        return EditarUsuarioRequest.builder()
                .nome("Rafael Atualizado")
                .email("novo@email.com")
                .imagemPerfil("https://picsum.photos/200")
                .build();
    }
}
