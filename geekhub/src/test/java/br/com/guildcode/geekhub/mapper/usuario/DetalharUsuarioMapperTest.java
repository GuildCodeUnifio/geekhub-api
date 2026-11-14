package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetalharUsuarioMapperTest {

    private final DetalharUsuarioMapper mapper = new DetalharUsuarioMapper();

    @Test
    @DisplayName("Deve mapear entidade Usuario para DetalharUsuarioResponse com todos os campos preenchidos")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();

        // Act
        DetalharUsuarioResponse response = mapper.toResponse(usuario);

        // Assert
        assertEquals(usuario.getId(), response.id());
        assertEquals(usuario.getNome(), response.nome());
        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getImagemPerfil(), response.imagemPerfil());
        assertEquals(usuario.getAtivo(), response.ativo());
        assertEquals(usuario.getCriadoEm(), response.criadoEm());
    }
}
