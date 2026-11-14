package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.EditarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditarUsuarioMapperTest {

    private final EditarUsuarioMapper mapper = new EditarUsuarioMapper();

    @Test
    @DisplayName("Deve mapear entidade Usuario para EditarUsuarioResponse com todos os campos preenchidos")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();

        // Act
        EditarUsuarioResponse response = mapper.toResponse(usuario);

        // Assert
        assertEquals(usuario.getId(), response.id());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getImagemPerfil(), response.imagemPerfil());
        assertEquals(usuario.getAtivo(), response.ativo());
        assertEquals(usuario.getCriadoEm(), response.criadoEm());
    }
}
