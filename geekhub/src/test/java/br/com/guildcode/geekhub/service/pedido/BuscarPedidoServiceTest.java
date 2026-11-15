package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPedidoServiceTest {

    @InjectMocks
    private BuscarPedidoService service;

    @Mock
    private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Deve retornar pedido quando ID encontrado no repositório")
    void deveBuscarPedidoPorId() {

        // Arrange
        Pedido pedido = PedidoFactory.get();
        when(pedidoRepository.findById(pedido.getId())).thenReturn(of(pedido));

        // Act
        Pedido resultado = service.buscarPorId(pedido.getId());

        // Assert
        assertEquals(pedido.getId(), resultado.getId());
        assertEquals(pedido.getValorTotal(), resultado.getValorTotal());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando pedido não encontrado por ID")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(empty());

        // Act
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        // Assert
        assertEquals("Pedido não encontrado", ex.getMessage());
    }
}
