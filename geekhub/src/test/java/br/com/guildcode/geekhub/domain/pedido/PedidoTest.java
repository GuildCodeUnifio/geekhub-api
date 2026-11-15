package br.com.guildcode.geekhub.domain.pedido;

import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoTest {

    @Test
    @DisplayName("Deve calcular valor total do pedido somando os subtotais dos itens")
    void deveCalcularValorTotalSomandoSubtotaisDosItens() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        ItemPedido itemExtra = ItemPedido.builder()
                .anuncioId(2L)
                .vendedorId(3L)
                .nomeProduto("Mangá Colecionável")
                .precoUnitario(new BigDecimal("49.95"))
                .quantidade(1)
                .subtotal(new BigDecimal("49.95"))
                .build();
        pedido.setItens(new ArrayList<>(List.of(pedido.getItens().getFirst(), itemExtra)));

        // Act
        pedido.calcularValorTotal();

        // Assert
        assertEquals(new BigDecimal("1899.95"), pedido.getValorTotal());
    }

    @Test
    @DisplayName("Deve atualizar estado do pedido e adicionar evento no histórico")
    void deveRegistrarEventoNoHistoricoEAtualizarEstado() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        LocalDateTime momentoEvento = LocalDateTime.of(2026, 11, 15, 20, 0);

        // Act
        pedido.registrarEvento(CONFIRMADO, momentoEvento);

        // Assert
        assertEquals(CONFIRMADO, pedido.getEstado());
        assertEquals(2, pedido.getHistorico().size());
        assertEquals(CONFIRMADO, pedido.getHistorico().getLast().getEstado());
        assertEquals(momentoEvento, pedido.getHistorico().getLast().getOcorridoEm());
    }
}
