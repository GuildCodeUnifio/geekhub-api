package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.DetalharUsuarioMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalharUsuarioServiceTest {

    @InjectMocks
    private DetalharUsuarioService service;

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private DetalharUsuarioMapper detalharUsuarioMapper;

    @Test
    @DisplayName("Deve detalhar usuário por ID com sucesso")
    void deveDetalharUsuarioComSucesso() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        DetalharUsuarioResponse expectedResponse = DetalharUsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .imagemPerfil(usuario.getImagemPerfil())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();

        when(buscarUsuarioService.buscarPorId(usuario.getId())).thenReturn(usuario);
        when(detalharUsuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        // Act
        DetalharUsuarioResponse response = service.detalhar(usuario.getId());

        // Assert
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(expectedResponse.email(), response.email());

        verify(buscarUsuarioService).buscarPorId(usuario.getId());
        verify(detalharUsuarioMapper).toResponse(usuario);
    }
}
