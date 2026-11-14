package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.EditarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.usuario.EditarUsuarioRequestFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.EditarUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import br.com.guildcode.geekhub.service.usuario.validator.EmailUnicoValidatorService;
import br.com.guildcode.geekhub.validator.usuario.EditarUsuarioValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarUsuarioServiceTest {

    @InjectMocks
    private EditarUsuarioService service;

    @Mock
    private EditarUsuarioValidator editarUsuarioValidator;

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private EmailUnicoValidatorService emailUnicoValidatorService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EditarUsuarioMapper editarUsuarioMapper;

    @Test
    @DisplayName("Deve editar usuário mantendo o mesmo e-mail sem validar unicidade")
    void deveEditarUsuarioMesmoEmail() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        EditarUsuarioRequest request = EditarUsuarioRequestFactory.get();
        EditarUsuarioResponse expectedResponse = EditarUsuarioResponse.builder()
                .id(usuario.getId())
                .nome(request.nome())
                .email(request.email())
                .imagemPerfil(request.imagemPerfil())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();

        doNothing().when(editarUsuarioValidator).validarDadosEdicao(request);
        when(buscarUsuarioService.buscarPorId(usuario.getId())).thenReturn(usuario);
        when(editarUsuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        // Act
        EditarUsuarioResponse response = service.editar(usuario.getId(), request);

        // Assert
        assertEquals(request.nome(), usuario.getNome());
        assertEquals(request.email(), usuario.getEmail());
        assertEquals(request.imagemPerfil(), usuario.getImagemPerfil());
        assertEquals(expectedResponse.nome(), response.nome());

        verify(editarUsuarioValidator).validarDadosEdicao(request);
        verify(emailUnicoValidatorService, never()).validar(request.email());
        verify(usuarioRepository).save(usuario);
        verify(editarUsuarioMapper).toResponse(usuario);
    }

    @Test
    @DisplayName("Deve validar unicidade do novo e-mail ao editar usuário com e-mail diferente")
    void deveEditarUsuarioComNovoEmailValido() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        EditarUsuarioRequest request = EditarUsuarioRequestFactory.comNovoEmail();
        EditarUsuarioResponse expectedResponse = EditarUsuarioResponse.builder()
                .id(usuario.getId())
                .nome(request.nome())
                .email(request.email())
                .imagemPerfil(request.imagemPerfil())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();

        doNothing().when(editarUsuarioValidator).validarDadosEdicao(request);
        when(buscarUsuarioService.buscarPorId(usuario.getId())).thenReturn(usuario);
        doNothing().when(emailUnicoValidatorService).validar(request.email());
        when(editarUsuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        // Act
        EditarUsuarioResponse response = service.editar(usuario.getId(), request);

        // Assert
        assertEquals(request.email(), usuario.getEmail());
        assertEquals(expectedResponse.email(), response.email());

        verify(editarUsuarioValidator).validarDadosEdicao(request);
        verify(emailUnicoValidatorService).validar(request.email());
        verify(usuarioRepository).save(usuario);
        verify(editarUsuarioMapper).toResponse(usuario);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao editar usuário para e-mail já existente")
    void deveLancarConflitoQuandoNovoEmailDuplicado() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        EditarUsuarioRequest request = EditarUsuarioRequestFactory.comNovoEmail();

        doNothing().when(editarUsuarioValidator).validarDadosEdicao(request);
        when(buscarUsuarioService.buscarPorId(usuario.getId())).thenReturn(usuario);
        doThrow(new ConflitoException("E-mail já cadastrado"))
                .when(emailUnicoValidatorService).validar(request.email());

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.editar(usuario.getId(), request)
        );

        assertEquals("E-mail já cadastrado", ex.getMessage());
        verify(editarUsuarioValidator).validarDadosEdicao(request);
        verify(usuarioRepository, never()).save(usuario);
    }
}
