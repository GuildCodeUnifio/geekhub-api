package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.pedido.validator.PedidoSemAvaliacaoValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class RemoverPedidoServiceTest {

    @Mock
    private BuscarPedidoService buscarPedidoService;

    @Mock
    private PedidoSemAvaliacaoValidatorService pedidoSemAvaliacaoValidatorService;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private RemoverPedidoService service;

    @Test
    @DisplayName("Deve remover pedido quando encontrado e sem avaliação vinculada")
    void deveRemoverPedidoComSucesso() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        when(buscarPedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);
        doNothing().when(pedidoSemAvaliacaoValidatorService).validar(pedido.getId());

        // Act
        service.remover(pedido.getId());

        // Assert
        verify(buscarPedidoService).buscarPorId(pedido.getId());
        verify(pedidoSemAvaliacaoValidatorService).validar(pedido.getId());
        verify(pedidoRepository).delete(pedido);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar remover pedido com avaliação vinculada")
    void deveLancarConflitoQuandoPedidoPossuiAvaliacao() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        when(buscarPedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);
        doThrow(new ConflitoException("Não é possível remover pedido pois possui avaliação vinculada"))
                .when(pedidoSemAvaliacaoValidatorService)
                .validar(pedido.getId());

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.remover(pedido.getId())
        );

        assertEquals("Não é possível remover pedido pois possui avaliação vinculada", ex.getMessage());
        verify(pedidoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar remover pedido inexistente")
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        // Arrange
        when(buscarPedidoService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.remover(999L)
        );

        assertEquals("Pedido não encontrado", ex.getMessage());
        verifyNoInteractions(pedidoSemAvaliacaoValidatorService);
        verify(pedidoRepository, never()).delete(any());
    }
}
