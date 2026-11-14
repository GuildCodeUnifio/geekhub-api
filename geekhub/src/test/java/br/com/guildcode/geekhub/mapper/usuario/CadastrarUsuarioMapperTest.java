package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.CadastrarUsuarioRequestFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CadastrarUsuarioMapperTest {

    private final CadastrarUsuarioMapper mapper = new CadastrarUsuarioMapper();

    @Test
    @DisplayName("Deve mapear CadastrarUsuarioRequest para entidade Usuario com todos os campos preenchidos")
    void deveMapearRequestParaEntidade() {

        // Arrange
        CadastrarUsuarioRequest request = CadastrarUsuarioRequestFactory.get();

        // Act
        Usuario entity = mapper.toEntity(request);

        // Assert
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.email(), entity.getEmail());
        assertEquals(request.imagemPerfil(), entity.getImagemPerfil());
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Deve mapear entidade Usuario para CadastrarUsuarioResponse com todos os campos preenchidos")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();

        // Act
        CadastrarUsuarioResponse response = mapper.toResponse(usuario);

        // Assert
        assertEquals(usuario.getId(), response.id());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getImagemPerfil(), response.imagemPerfil());
        assertEquals(usuario.getCriadoEm(), response.criadoEm());
    }
}
