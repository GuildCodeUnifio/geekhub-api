package br.com.guildcode.geekhub.domain.pedido;

import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @DisplayName("Deve calcular quantidade total de unidades somando todos os itens do pedido")
    void deveCalcularQuantidadeTotalDeItens() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        ItemPedido itemExtra = ItemPedido.builder()
                .anuncioId(2L)
                .vendedorId(3L)
                .nomeProduto("Mangá Colecionável")
                .precoUnitario(new BigDecimal("49.90"))
                .quantidade(2)
                .subtotal(new BigDecimal("99.80"))
                .build();
        pedido.setItens(new ArrayList<>(List.of(pedido.getItens().getFirst(), itemExtra)));

        // Act
        Integer quantidadeItens = pedido.calcularQuantidadeItens();

        // Assert
        assertEquals(3, quantidadeItens);
    }

    @Test
    @DisplayName("Deve identificar corretamente se usuário é comprador ou vendedor participante do pedido")
    void deveIdentificarCompradorEVendedorDoPedido() {
        // Arrange
        Pedido pedido = PedidoFactory.get();

        // Act & Assert
        assertTrue(pedido.pertenceAoComprador(1L));
        assertFalse(pedido.pertenceAoComprador(50L));
        assertTrue(pedido.possuiVendedor(2L));
        assertFalse(pedido.possuiVendedor(50L));
    }

    @Test
    @DisplayName("Deve identificar quando pedido está no estado CONCLUIDO")
    void deveIdentificarQuandoPedidoEstaConcluido() {
        // Arrange
        Pedido pedido = PedidoFactory.get();

        // Act & Assert
        assertFalse(pedido.isConcluido());

        pedido.setEstado(CONCLUIDO);
        assertTrue(pedido.isConcluido());
    }

    @Test
    @DisplayName("Deve retornar ID do vendedor principal dos itens ou nulo quando sem itens")
    void deveObterVendedorIdDosItensOuNuloQuandoSemItens() {
        // Arrange
        Pedido pedido = PedidoFactory.get();

        // Act & Assert
        assertEquals(2L, pedido.obterVendedorId());

        pedido.setItens(new ArrayList<>());
        assertNull(pedido.obterVendedorId());

        pedido.setItens(null);
        assertNull(pedido.obterVendedorId());
    }
}
