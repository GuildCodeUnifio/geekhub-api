package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.avaliacao.validator.AutorAvaliacaoValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAvaliacaoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarAvaliacaoService buscarAvaliacaoService;

    @Mock
    private AutorAvaliacaoValidatorService autorAvaliacaoValidatorService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private RemoverAvaliacaoService service;

    @Test
    @DisplayName("Deve remover avaliação e atualizar pedido.avaliado para false")
    void deveRemoverAvaliacaoEReabrirPedidoComSucesso() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Pedido pedido = avaliacao.getPedido();
        pedido.setAvaliado(TRUE);
        Usuario autor = avaliacao.getAutor();

        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarAvaliacaoService.buscarPorId(avaliacao.getId())).thenReturn(avaliacao);
        doNothing().when(autorAvaliacaoValidatorService).validarAutorOuAdmin(avaliacao, autor);

        // Act
        service.remover(avaliacao.getId());

        // Assert
        assertFalse(pedido.getAvaliado());
        verify(autorAvaliacaoValidatorService).validarAutorOuAdmin(avaliacao, autor);
        verify(avaliacaoRepository).delete(avaliacao);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário comum não autor tentar remover avaliação")
    void deveLancarAccessDeniedQuandoUsuarioSemPermissao() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario outroUsuario = UsuarioFactory.get();
        outroUsuario.setId(77L);

        when(usuarioAutenticadoService.get()).thenReturn(outroUsuario);
        when(buscarAvaliacaoService.buscarPorId(avaliacao.getId())).thenReturn(avaliacao);
        doThrow(new AccessDeniedException("Você não possui permissão para remover esta avaliação"))
                .when(autorAvaliacaoValidatorService)
                .validarAutorOuAdmin(avaliacao, outroUsuario);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.remover(avaliacao.getId())
        );

        assertEquals("Você não possui permissão para remover esta avaliação", ex.getMessage());
        verifyNoInteractions(avaliacaoRepository, pedidoRepository);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar remover avaliação inexistente")
    void deveLancarExcecaoQuandoAvaliacaoNaoExistir() {
        // Arrange
        Usuario autor = UsuarioFactory.get();
        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarAvaliacaoService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Avaliação não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.remover(999L)
        );

        assertEquals("Avaliação não encontrada", ex.getMessage());
        verifyNoInteractions(autorAvaliacaoValidatorService, avaliacaoRepository, pedidoRepository);
    }
}
