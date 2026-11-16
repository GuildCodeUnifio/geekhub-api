package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.EditarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.factory.avaliacao.EditarAvaliacaoRequestFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.avaliacao.EditarAvaliacaoMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.avaliacao.validator.AutorAvaliacaoValidatorService;
import br.com.guildcode.geekhub.validator.avaliacao.EditarAvaliacaoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarAvaliacaoServiceTest {

    @Mock
    private EditarAvaliacaoValidator editarAvaliacaoValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarAvaliacaoService buscarAvaliacaoService;

    @Mock
    private AutorAvaliacaoValidatorService autorAvaliacaoValidatorService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private EditarAvaliacaoMapper editarAvaliacaoMapper;

    @InjectMocks
    private EditarAvaliacaoService service;

    @Test
    @DisplayName("Deve editar nota e comentário da avaliação quando usuário autenticado for o autor")
    void deveEditarAvaliacaoComSucesso() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario autor = avaliacao.getAutor();
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();
        EditarAvaliacaoResponse expectedResponse = EditarAvaliacaoResponse.builder()
                .id(avaliacao.getId())
                .pedidoId(avaliacao.getPedido().getId())
                .vendedorId(avaliacao.getVendedor().getId())
                .autorId(autor.getId())
                .autorNome(autor.getNome())
                .nota(request.nota())
                .comentario(request.comentario())
                .criadoEm(avaliacao.getCriadoEm())
                .build();

        doNothing().when(editarAvaliacaoValidator).validarDadosEdicao(request);
        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarAvaliacaoService.buscarPorId(avaliacao.getId())).thenReturn(avaliacao);
        doNothing().when(autorAvaliacaoValidatorService).validarAutor(avaliacao, autor);
        when(avaliacaoRepository.save(avaliacao)).thenReturn(avaliacao);
        when(editarAvaliacaoMapper.toResponse(avaliacao)).thenReturn(expectedResponse);

        // Act
        EditarAvaliacaoResponse response = service.editar(avaliacao.getId(), request);

        // Assert
        assertEquals(expectedResponse, response);
        assertEquals(request.nota(), avaliacao.getNota());
        assertEquals(request.comentario(), avaliacao.getComentario());
        verify(editarAvaliacaoValidator).validarDadosEdicao(request);
        verify(autorAvaliacaoValidatorService).validarAutor(avaliacao, autor);
        verify(avaliacaoRepository).save(avaliacao);
        verify(editarAvaliacaoMapper).toResponse(avaliacao);
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando dados de edição forem inválidos")
    void deveLancarNegocioExceptionQuandoDadosInvalidos() {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequest.builder()
                .nota(0)
                .comentario("Inválido")
                .build();
        doThrow(new NegocioException("A nota da avaliação deve ser entre 1 e 5"))
                .when(editarAvaliacaoValidator)
                .validarDadosEdicao(request);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> service.editar(1L, request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
        verifyNoInteractions(
                usuarioAutenticadoService,
                buscarAvaliacaoService,
                autorAvaliacaoValidatorService,
                avaliacaoRepository,
                editarAvaliacaoMapper
        );
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário autenticado não for o autor da avaliação")
    void deveLancarAccessDeniedQuandoNaoForAutor() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario outroUsuario = UsuarioFactory.getAdmin();
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();

        doNothing().when(editarAvaliacaoValidator).validarDadosEdicao(request);
        when(usuarioAutenticadoService.get()).thenReturn(outroUsuario);
        when(buscarAvaliacaoService.buscarPorId(avaliacao.getId())).thenReturn(avaliacao);
        doThrow(new AccessDeniedException("Você não possui permissão para editar esta avaliação"))
                .when(autorAvaliacaoValidatorService)
                .validarAutor(avaliacao, outroUsuario);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.editar(avaliacao.getId(), request)
        );

        assertEquals("Você não possui permissão para editar esta avaliação", ex.getMessage());
        verify(avaliacaoRepository, never()).save(any());
        verifyNoInteractions(editarAvaliacaoMapper);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar editar avaliação inexistente")
    void deveLancarExcecaoQuandoAvaliacaoNaoExistir() {
        // Arrange
        Usuario autor = UsuarioFactory.get();
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();

        doNothing().when(editarAvaliacaoValidator).validarDadosEdicao(request);
        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarAvaliacaoService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Avaliação não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.editar(999L, request)
        );

        assertEquals("Avaliação não encontrada", ex.getMessage());
        verifyNoInteractions(autorAvaliacaoValidatorService, avaliacaoRepository, editarAvaliacaoMapper);
    }
}
