package br.com.guildcode.geekhub.service.avaliacao.validator;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElegibilidadeAvaliacaoValidatorServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private ElegibilidadeAvaliacaoValidatorService service;

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário autenticado não for o comprador do pedido")
    void deveLancarAccessDeniedQuandoUsuarioNaoForComprador() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario outroUsuario = UsuarioFactory.get();
        outroUsuario.setId(99L);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.validar(pedido, outroUsuario)
        );

        assertEquals("Você não possui permissão para avaliar este pedido", ex.getMessage());
        verifyNoInteractions(avaliacaoRepository);
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando pedido não estiver no estado CONCLUIDO")
    void deveLancarNegocioExceptionQuandoPedidoNaoEstiverConcluido() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario comprador = pedido.getComprador();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> service.validar(pedido, comprador)
        );

        assertEquals("Somente pedidos concluídos podem ser avaliados", ex.getMessage());
        verifyNoInteractions(avaliacaoRepository);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando pedido já estiver marcado como avaliado")
    void deveLancarConflitoQuandoPedidoJaEstiverMarcadoComoAvaliado() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(CONCLUIDO);
        pedido.setAvaliado(TRUE);
        Usuario comprador = pedido.getComprador();

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.validar(pedido, comprador)
        );

        assertEquals("Este pedido já foi avaliado", ex.getMessage());
        verifyNoInteractions(avaliacaoRepository);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando já existir avaliação registrada para o pedido no repositório")
    void deveLancarConflitoQuandoExistirAvaliacaoNoRepositorio() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(CONCLUIDO);
        pedido.setAvaliado(FALSE);
        Usuario comprador = pedido.getComprador();
        when(avaliacaoRepository.existsByPedidoId(pedido.getId())).thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.validar(pedido, comprador)
        );

        assertEquals("Este pedido já foi avaliado", ex.getMessage());
        verify(avaliacaoRepository).existsByPedidoId(pedido.getId());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando pedido não possuir itens para identificar o vendedor")
    void deveLancarNegocioExceptionQuandoPedidoNaoPossuirItens() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(CONCLUIDO);
        pedido.setAvaliado(FALSE);
        pedido.setItens(Collections.emptyList());
        Usuario comprador = pedido.getComprador();
        when(avaliacaoRepository.existsByPedidoId(pedido.getId())).thenReturn(FALSE);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> service.validar(pedido, comprador)
        );

        assertEquals("O pedido não possui itens para identificar o vendedor", ex.getMessage());
        verify(avaliacaoRepository).existsByPedidoId(pedido.getId());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando pedido for elegível para avaliação")
    void deveValidarPedidoElegivelComSucesso() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(CONCLUIDO);
        pedido.setAvaliado(FALSE);
        Usuario comprador = pedido.getComprador();
        when(avaliacaoRepository.existsByPedidoId(pedido.getId())).thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> service.validar(pedido, comprador));
        verify(avaliacaoRepository).existsByPedidoId(pedido.getId());
    }
}
