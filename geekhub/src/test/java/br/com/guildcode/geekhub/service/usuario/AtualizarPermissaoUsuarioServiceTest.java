package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AtualizarPermissaoUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.usuario.AtualizarPermissaoUsuarioRequestFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.AtualizarPermissaoUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.usuario.validator.AlteracaoPropriaPermissaoValidatorService;
import br.com.guildcode.geekhub.validator.usuario.AtualizarPermissaoUsuarioValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarPermissaoUsuarioServiceTest {

    @InjectMocks
    private AtualizarPermissaoUsuarioService service;

    @Mock
    private AtualizarPermissaoUsuarioValidator atualizarPermissaoUsuarioValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private AlteracaoPropriaPermissaoValidatorService alteracaoPropriaPermissaoValidatorService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AtualizarPermissaoUsuarioMapper atualizarPermissaoUsuarioMapper;

    @Test
    @DisplayName("Deve promover usuário comum para ADMIN com sucesso")
    void devePromoverUsuarioParaAdminComSucesso() {

        // Arrange
        Usuario adminAutenticado = UsuarioFactory.getAdmin();
        Usuario usuarioAlvo = UsuarioFactory.get();
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();
        AtualizarPermissaoUsuarioResponse expectedResponse = AtualizarPermissaoUsuarioResponse.builder()
                .id(usuarioAlvo.getId())
                .nome(usuarioAlvo.getNome())
                .email(usuarioAlvo.getEmail())
                .admin(TRUE)
                .permissoes(List.of("USUARIO", "ADMIN"))
                .build();

        doNothing().when(atualizarPermissaoUsuarioValidator).validarDadosAtualizacao(request);
        when(usuarioAutenticadoService.get()).thenReturn(adminAutenticado);
        when(buscarUsuarioService.buscarPorId(usuarioAlvo.getId())).thenReturn(usuarioAlvo);
        doNothing().when(alteracaoPropriaPermissaoValidatorService).validar(adminAutenticado, usuarioAlvo);
        when(usuarioRepository.save(usuarioAlvo)).thenReturn(usuarioAlvo);
        when(atualizarPermissaoUsuarioMapper.toResponse(usuarioAlvo)).thenReturn(expectedResponse);

        // Act
        AtualizarPermissaoUsuarioResponse response = service.atualizar(usuarioAlvo.getId(), request);

        // Assert
        assertTrue(usuarioAlvo.isAdmin());
        assertTrue(response.admin());
        assertEquals(List.of("USUARIO", "ADMIN"), response.permissoes());

        verify(atualizarPermissaoUsuarioValidator).validarDadosAtualizacao(request);
        verify(usuarioAutenticadoService).get();
        verify(buscarUsuarioService).buscarPorId(usuarioAlvo.getId());
        verify(alteracaoPropriaPermissaoValidatorService).validar(adminAutenticado, usuarioAlvo);
        verify(usuarioRepository).save(usuarioAlvo);
        verify(atualizarPermissaoUsuarioMapper).toResponse(usuarioAlvo);
    }

    @Test
    @DisplayName("Deve rebaixar usuário ADMIN para usuário comum com sucesso")
    void deveRebaixarUsuarioAdminComSucesso() {

        // Arrange
        Usuario adminAutenticado = UsuarioFactory.getAdmin();
        Usuario outroAdmin = UsuarioFactory.getAdmin();
        outroAdmin.setId(2L);
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.rebaixarAdmin();
        AtualizarPermissaoUsuarioResponse expectedResponse = AtualizarPermissaoUsuarioResponse.builder()
                .id(outroAdmin.getId())
                .nome(outroAdmin.getNome())
                .email(outroAdmin.getEmail())
                .admin(FALSE)
                .permissoes(List.of("USUARIO"))
                .build();

        doNothing().when(atualizarPermissaoUsuarioValidator).validarDadosAtualizacao(request);
        when(usuarioAutenticadoService.get()).thenReturn(adminAutenticado);
        when(buscarUsuarioService.buscarPorId(outroAdmin.getId())).thenReturn(outroAdmin);
        doNothing().when(alteracaoPropriaPermissaoValidatorService).validar(adminAutenticado, outroAdmin);
        when(usuarioRepository.save(outroAdmin)).thenReturn(outroAdmin);
        when(atualizarPermissaoUsuarioMapper.toResponse(outroAdmin)).thenReturn(expectedResponse);

        // Act
        AtualizarPermissaoUsuarioResponse response = service.atualizar(outroAdmin.getId(), request);

        // Assert
        assertFalse(outroAdmin.isAdmin());
        assertFalse(response.admin());
        assertEquals(List.of("USUARIO"), outroAdmin.obterNomesPermissoes());
        assertEquals(List.of("USUARIO"), response.permissoes());
    }

    @Test
    @DisplayName("Deve lançar exceção quando dados da requisição forem inválidos")
    void deveLancarExcecaoQuandoDadosInvalidos() {

        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();
        doThrow(new NegocioException("O indicador de administrador é obrigatório"))
                .when(atualizarPermissaoUsuarioValidator).validarDadosAtualizacao(request);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.atualizar(1L, request));

        verifyNoInteractions(usuarioAutenticadoService, buscarUsuarioService,
                alteracaoPropriaPermissaoValidatorService, usuarioRepository, atualizarPermissaoUsuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário alvo não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {

        // Arrange
        Usuario adminAutenticado = UsuarioFactory.getAdmin();
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();

        doNothing().when(atualizarPermissaoUsuarioValidator).validarDadosAtualizacao(request);
        when(usuarioAutenticadoService.get()).thenReturn(adminAutenticado);
        when(buscarUsuarioService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(999L, request));

        verifyNoInteractions(alteracaoPropriaPermissaoValidatorService, usuarioRepository,
                atualizarPermissaoUsuarioMapper);
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando administrador tentar alterar a própria permissão")
    void deveLancarExcecaoQuandoAlterarPropriaPermissao() {

        // Arrange
        Usuario adminAutenticado = UsuarioFactory.getAdmin();
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.rebaixarAdmin();

        doNothing().when(atualizarPermissaoUsuarioValidator).validarDadosAtualizacao(request);
        when(usuarioAutenticadoService.get()).thenReturn(adminAutenticado);
        when(buscarUsuarioService.buscarPorId(adminAutenticado.getId())).thenReturn(adminAutenticado);
        doThrow(new NegocioException("Não é possível alterar a própria permissão de administrador"))
                .when(alteracaoPropriaPermissaoValidatorService).validar(adminAutenticado, adminAutenticado);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.atualizar(adminAutenticado.getId(), request));

        verifyNoInteractions(usuarioRepository, atualizarPermissaoUsuarioMapper);
    }
}
