package br.com.guildcode.geekhub.mapper.dashboard;

import br.com.guildcode.geekhub.controller.dashboard.dto.response.CategoriaKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.EstadoPedidoKpiResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.repository.anuncio.ResumoCategoriaAnuncioProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultarDashboardMapperTest {

    private final ConsultarDashboardMapper mapper = new ConsultarDashboardMapper();

    @Test
    @DisplayName("Deve mapear indicador de estado de pedido corretamente")
    void deveMapearEstadoPedidoKpiResponse() {

        // Arrange & Act
        EstadoPedidoKpiResponse response = mapper.toEstadoPedidoKpiResponse(CONCLUIDO, 4L, 50);

        // Assert
        assertEquals(CONCLUIDO, response.estado());
        assertEquals(4L, response.quantidade());
        assertEquals(50, response.percentual());
    }

    @Test
    @DisplayName("Deve mapear projeção de categoria de anúncio para CategoriaKpiResponse")
    void deveMapearCategoriaKpiResponse() {

        // Arrange
        ResumoCategoriaAnuncioProjection projection = new ResumoCategoriaAnuncioProjection() {
            @Override
            public String getCategoriaNome() {
                return "Figuras colecionáveis";
            }

            @Override
            public Long getQuantidadeAnuncios() {
                return 3L;
            }

            @Override
            public Long getEstoqueTotal() {
                return 8L;
            }
        };

        // Act
        CategoriaKpiResponse response = mapper.toCategoriaKpiResponse(projection, 75);

        // Assert
        assertEquals("Figuras colecionáveis", response.nome());
        assertEquals(3L, response.quantidadeAnuncios());
        assertEquals(8L, response.estoqueTotal());
        assertEquals(75, response.percentual());
    }

    @Test
    @DisplayName("Deve mapear resposta completa do dashboard com pedidos recentes")
    void deveMapearDashboardCompletoComPedidosRecentes() {

        // Arrange
        Pedido pedido = PedidoFactory.get();
        List<EstadoPedidoKpiResponse> pedidosPorEstado = List.of(
                mapper.toEstadoPedidoKpiResponse(CRIADO, 1L, 100)
        );
        List<CategoriaKpiResponse> anunciosPorCategoria = List.of(
                CategoriaKpiResponse.builder()
                        .nome("Figuras colecionáveis")
                        .quantidadeAnuncios(2L)
                        .estoqueTotal(5L)
                        .percentual(100)
                        .build()
        );

        // Act
        ConsultarDashboardResponse response = mapper.toResponse(
                new BigDecimal("1850.00"),
                new BigDecimal("1850.00"),
                1L,
                0L,
                1L,
                0L,
                0,
                2L,
                2L,
                0L,
                5L,
                4L,
                4.8,
                3L,
                pedidosPorEstado,
                anunciosPorCategoria,
                List.of(pedido)
        );

        // Assert
        assertEquals(new BigDecimal("1850.00"), response.receitaTotal());
        assertEquals(new BigDecimal("1850.00"), response.ticketMedio());
        assertEquals(1L, response.totalPedidos());
        assertEquals(0L, response.pedidosConcluidos());
        assertEquals(1L, response.pedidosEmAndamento());
        assertEquals(0L, response.pedidosCancelados());
        assertEquals(0, response.taxaConclusao());
        assertEquals(2L, response.totalAnuncios());
        assertEquals(2L, response.anunciosDisponiveis());
        assertEquals(0L, response.anunciosIndisponiveis());
        assertEquals(5L, response.unidadesEmEstoque());
        assertEquals(4L, response.totalCategoriasAtivas());
        assertEquals(4.8, response.notaMediaAvaliacoes());
        assertEquals(3L, response.totalAvaliacoes());
        assertEquals(pedidosPorEstado, response.pedidosPorEstado());
        assertEquals(anunciosPorCategoria, response.anunciosPorCategoria());
        assertEquals(1, response.pedidosRecentes().size());
        assertEquals(pedido.getId(), response.pedidosRecentes().getFirst().id());
        assertEquals(pedido.getCompradorNome(), response.pedidosRecentes().getFirst().compradorNome());
        assertEquals(pedido.getEstado(), response.pedidosRecentes().getFirst().estado());
        assertEquals(pedido.getValorTotal(), response.pedidosRecentes().getFirst().valorTotal());
        assertEquals(pedido.calcularQuantidadeItens(), response.pedidosRecentes().getFirst().quantidadeItens());
        assertEquals(pedido.getCriadoEm(), response.pedidosRecentes().getFirst().criadoEm());
    }

    @Test
    @DisplayName("Deve mapear resposta do dashboard vazia sem pedidos recentes e nota média nula")
    void deveMapearDashboardVazio() {

        // Act
        ConsultarDashboardResponse response = mapper.toResponse(
                new BigDecimal("0.00"),
                new BigDecimal("0.00"),
                0L,
                0L,
                0L,
                0L,
                0,
                0L,
                0L,
                0L,
                0L,
                0L,
                null,
                0L,
                List.of(),
                List.of(),
                List.of()
        );

        // Assert
        assertNull(response.notaMediaAvaliacoes());
        assertTrue(response.pedidosPorEstado().isEmpty());
        assertTrue(response.anunciosPorCategoria().isEmpty());
        assertTrue(response.pedidosRecentes().isEmpty());
    }
}
