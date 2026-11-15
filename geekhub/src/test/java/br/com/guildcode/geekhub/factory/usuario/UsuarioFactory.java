package br.com.guildcode.geekhub.factory.usuario;

import br.com.guildcode.geekhub.domain.usuario.Permissao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

public class UsuarioFactory {

    public static Usuario get() {

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Rafael Garcia")
                .email("rafael@email.com")
                .senha("$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG")
                .imagemPerfil("http://foto.jpg")
                .ativo(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        usuario.adicionarPermissao(Permissao.builder().id(1L).nome("USUARIO").build());

        return usuario;
    }

    public static Usuario getAdmin() {

        Usuario admin = Usuario.builder()
                .id(99L)
                .nome("Administrador GeekHub")
                .email("admin@geekhub.com")
                .senha("$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG")
                .imagemPerfil("https://picsum.photos/200")
                .ativo(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        admin.adicionarPermissao(Permissao.builder().id(1L).nome("ADMIN").build());
        admin.adicionarPermissao(Permissao.builder().id(2L).nome("USUARIO").build());

        return admin;
    }
}
