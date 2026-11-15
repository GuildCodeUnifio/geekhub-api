package br.com.guildcode.geekhub.domain.usuario;

import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    @DisplayName("Deve adicionar permissão vinculando usuário bidirecionalmente")
    void deveAdicionarPermissaoVinculandoUsuario() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        Permissao novaPermissao = Permissao.builder().nome("ADMIN").build();

        // Act
        usuario.adicionarPermissao(novaPermissao);

        // Assert
        assertEquals(2, usuario.getPermissoes().size());
        assertEquals(usuario, novaPermissao.getUsuario());
    }

    @Test
    @DisplayName("Deve retornar true quando usuário possuir permissão ADMIN")
    void deveRetornarTrueQuandoUsuarioForAdmin() {

        // Arrange
        Usuario admin = UsuarioFactory.getAdmin();

        // Act
        Boolean isAdmin = admin.isAdmin();

        // Assert
        assertTrue(isAdmin);
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não possuir permissão ADMIN")
    void deveRetornarFalseQuandoUsuarioNaoForAdmin() {

        // Arrange
        Usuario usuarioComum = UsuarioFactory.get();

        // Act
        Boolean isAdmin = usuarioComum.isAdmin();

        // Assert
        assertFalse(isAdmin);
    }
}
