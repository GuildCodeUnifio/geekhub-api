package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.AtualizarPermissaoUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtualizarPermissaoUsuarioMapperTest {

    private final AtualizarPermissaoUsuarioMapper mapper = new AtualizarPermissaoUsuarioMapper();

    @Test
    @DisplayName("Deve mapear usuário administrador para AtualizarPermissaoUsuarioResponse")
    void deveMapearUsuarioAdmin() {

        // Arrange
        Usuario admin = UsuarioFactory.getAdmin();

        // Act
        AtualizarPermissaoUsuarioResponse response = mapper.toResponse(admin);

        // Assert
        assertEquals(admin.getId(), response.id());
        assertEquals(admin.getNome(), response.nome());
        assertEquals(admin.getEmail(), response.email());
        assertTrue(response.admin());
        assertEquals(List.of("ADMIN", "USUARIO"), response.permissoes());
    }

    @Test
    @DisplayName("Deve mapear usuário comum para AtualizarPermissaoUsuarioResponse")
    void deveMapearUsuarioComum() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();

        // Act
        AtualizarPermissaoUsuarioResponse response = mapper.toResponse(usuario);

        // Assert
        assertEquals(usuario.getId(), response.id());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertFalse(response.admin());
        assertEquals(List.of("USUARIO"), response.permissoes());
    }
}
