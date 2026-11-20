package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.DetalharUsuarioMapper;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.usuario.validator.AcessoUsuarioValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalharUsuarioServiceTest {

    @InjectMocks
    private DetalharUsuarioService service;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private AcessoUsuarioValidatorService acessoUsuarioValidatorService;

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

        when(usuarioAutenticadoService.get()).thenReturn(usuario);
        doNothing().when(acessoUsuarioValidatorService).validarVisualizacao(usuario.getId(), usuario);
        when(buscarUsuarioService.buscarPorId(usuario.getId())).thenReturn(usuario);
        when(detalharUsuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        // Act
        DetalharUsuarioResponse response = service.detalhar(usuario.getId());

        // Assert
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(expectedResponse.email(), response.email());

        verify(usuarioAutenticadoService).get();
        verify(acessoUsuarioValidatorService).validarVisualizacao(usuario.getId(), usuario);
        verify(buscarUsuarioService).buscarPorId(usuario.getId());
        verify(detalharUsuarioMapper).toResponse(usuario);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao detalhar usuário sem permissão de acesso")
    void deveLancarAccessDeniedQuandoSemPermissao() {

        // Arrange
        Usuario usuarioAutenticado = UsuarioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(usuarioAutenticado);
        doThrow(new AccessDeniedException("Você não possui permissão para visualizar este usuário"))
                .when(acessoUsuarioValidatorService).validarVisualizacao(2L, usuarioAutenticado);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.detalhar(2L)
        );

        assertEquals("Você não possui permissão para visualizar este usuário", ex.getMessage());
        verifyNoInteractions(buscarUsuarioService, detalharUsuarioMapper);
    }
}
