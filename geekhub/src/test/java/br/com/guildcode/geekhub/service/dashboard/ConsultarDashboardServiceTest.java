package br.com.guildcode.geekhub.service.dashboard;

import br.com.guildcode.geekhub.controller.dashboard.dto.response.CategoriaKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.EstadoPedidoKpiResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.mapper.dashboard.ConsultarDashboardMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.anuncio.ResumoCategoriaAnuncioProjection;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarDashboardServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private ConsultarDashboardMapper consultarDashboardMapper;

    @InjectMocks
    private ConsultarDashboardService service;

    @Test
    @DisplayName("Deve calcular e consolidar todos os indicadores operacionais do dashboard")
    void deveCalcularIndicadoresCompletosDoDashboard() {

        // Arrange
        Pedido pedidoRecente = PedidoFactory.get();
        List<Pedido> recentes = List.of(pedidoRecente);

        when(pedidoRepository.countByEstado(CRIADO)).thenReturn(1L);
        when(pedidoRepository.countByEstado(CONFIRMADO)).thenReturn(1L);
        when(pedidoRepository.countByEstado(ENVIADO)).thenReturn(1L);
        when(pedidoRepository.countByEstado(CONCLUIDO)).thenReturn(3L);
        when(pedidoRepository.countByEstado(CANCELADO)).thenReturn(1L);
        when(pedidoRepository.somarValorTotalExcluindoEstado(CANCELADO)).thenReturn(new BigDecimal("1000.00"));
        when(pedidoRepository.findTop5ByOrderByCriadoEmDesc()).thenReturn(recentes);

        EstadoPedidoKpiResponse kpiCriado = EstadoPedidoKpiResponse.builder().estado(CRIADO).quantidade(1L).percentual(14).build();
        EstadoPedidoKpiResponse kpiConfirmado = EstadoPedidoKpiResponse.builder().estado(CONFIRMADO).quantidade(1L).percentual(14).build();
        EstadoPedidoKpiResponse kpiEnviado = EstadoPedidoKpiResponse.builder().estado(ENVIADO).quantidade(1L).percentual(14).build();
        EstadoPedidoKpiResponse kpiConcluido = EstadoPedidoKpiResponse.builder().estado(CONCLUIDO).quantidade(3L).percentual(43).build();
        EstadoPedidoKpiResponse kpiCancelado = EstadoPedidoKpiResponse.builder().estado(CANCELADO).quantidade(1L).percentual(14).build();

        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(CRIADO, 1L, 14)).thenReturn(kpiCriado);
        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(CONFIRMADO, 1L, 14)).thenReturn(kpiConfirmado);
        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(ENVIADO, 1L, 14)).thenReturn(kpiEnviado);
        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(CONCLUIDO, 3L, 43)).thenReturn(kpiConcluido);
        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(CANCELADO, 1L, 14)).thenReturn(kpiCancelado);

        ResumoCategoriaAnuncioProjection catFiguras = mock(ResumoCategoriaAnuncioProjection.class);
        when(catFiguras.getQuantidadeAnuncios()).thenReturn(3L);
        ResumoCategoriaAnuncioProjection catJogos = mock(ResumoCategoriaAnuncioProjection.class);
        when(catJogos.getQuantidadeAnuncios()).thenReturn(1L);

        CategoriaKpiResponse respFiguras = CategoriaKpiResponse.builder()
                .nome("Figuras")
                .quantidadeAnuncios(3L)
                .estoqueTotal(10L)
                .percentual(75)
                .build();
        CategoriaKpiResponse respJogos = CategoriaKpiResponse.builder()
                .nome("Jogos")
                .quantidadeAnuncios(1L)
                .estoqueTotal(2L)
                .percentual(25)
                .build();

        when(anuncioRepository.count()).thenReturn(4L);
        when(anuncioRepository.contarDisponiveis()).thenReturn(3L);
        when(anuncioRepository.somarUnidadesEmEstoque()).thenReturn(12L);
        when(categoriaRepository.countByAtivaTrue()).thenReturn(2L);
        when(anuncioRepository.agruparPorCategoria()).thenReturn(List.of(catFiguras, catJogos));
        when(consultarDashboardMapper.toCategoriaKpiResponse(catFiguras, 75)).thenReturn(respFiguras);
        when(consultarDashboardMapper.toCategoriaKpiResponse(catJogos, 25)).thenReturn(respJogos);

        when(avaliacaoRepository.count()).thenReturn(3L);
        when(avaliacaoRepository.calcularNotaMediaGeral()).thenReturn(4.666666);

        List<EstadoPedidoKpiResponse> listaEstados = List.of(
                kpiCriado, kpiConfirmado, kpiEnviado, kpiConcluido, kpiCancelado);
        List<CategoriaKpiResponse> listaCategorias = List.of(respFiguras, respJogos);

        ConsultarDashboardResponse expectedResponse = ConsultarDashboardResponse.builder()
                .receitaTotal(new BigDecimal("1000.00"))
                .ticketMedio(new BigDecimal("166.67"))
                .totalPedidos(7L)
                .pedidosConcluidos(3L)
                .pedidosEmAndamento(3L)
                .pedidosCancelados(1L)
                .taxaConclusao(43)
                .totalAnuncios(4L)
                .anunciosDisponiveis(3L)
                .anunciosIndisponiveis(1L)
                .unidadesEmEstoque(12L)
                .totalCategoriasAtivas(2L)
                .notaMediaAvaliacoes(4.7)
                .totalAvaliacoes(3L)
                .pedidosPorEstado(listaEstados)
                .anunciosPorCategoria(listaCategorias)
                .pedidosRecentes(List.of())
                .build();

        when(consultarDashboardMapper.toResponse(
                new BigDecimal("1000.00"),
                new BigDecimal("166.67"),
                7L,
                3L,
                3L,
                1L,
                43,
                4L,
                3L,
                1L,
                12L,
                2L,
                4.7,
                3L,
                listaEstados,
                listaCategorias,
                recentes
        )).thenReturn(expectedResponse);

        // Act
        ConsultarDashboardResponse response = service.consultar();

        // Assert
        assertEquals(expectedResponse, response);
        verify(pedidoRepository).somarValorTotalExcluindoEstado(CANCELADO);
        verify(pedidoRepository).findTop5ByOrderByCriadoEmDesc();
        verify(anuncioRepository).somarUnidadesEmEstoque();
        verify(anuncioRepository).agruparPorCategoria();
        verify(avaliacaoRepository).calcularNotaMediaGeral();
    }

    @Test
    @DisplayName("Deve retornar indicadores zerados e nota média nula quando não houver registros")
    void deveRetornarIndicadoresZeradosQuandoBaseEstiverVazia() {

        // Arrange
        when(pedidoRepository.countByEstado(any())).thenReturn(0L);
        when(anuncioRepository.count()).thenReturn(0L);
        when(anuncioRepository.contarDisponiveis()).thenReturn(0L);
        when(categoriaRepository.countByAtivaTrue()).thenReturn(0L);
        when(avaliacaoRepository.count()).thenReturn(0L);

        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(any(EstadoPedido.class), eq(0L), eq(0)))
                .thenAnswer(invocation -> EstadoPedidoKpiResponse.builder()
                        .estado(invocation.getArgument(0))
                        .quantidade(0L)
                        .percentual(0)
                        .build());

        ConsultarDashboardResponse expectedResponse = ConsultarDashboardResponse.builder()
                .receitaTotal(new BigDecimal("0.00"))
                .ticketMedio(new BigDecimal("0.00"))
                .totalPedidos(0L)
                .pedidosConcluidos(0L)
                .pedidosEmAndamento(0L)
                .pedidosCancelados(0L)
                .taxaConclusao(0)
                .totalAnuncios(0L)
                .anunciosDisponiveis(0L)
                .anunciosIndisponiveis(0L)
                .unidadesEmEstoque(0L)
                .totalCategoriasAtivas(0L)
                .notaMediaAvaliacoes(null)
                .totalAvaliacoes(0L)
                .pedidosPorEstado(List.of())
                .anunciosPorCategoria(List.of())
                .pedidosRecentes(List.of())
                .build();

        when(consultarDashboardMapper.toResponse(
                eq(new BigDecimal("0.00")),
                eq(new BigDecimal("0.00")),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(0),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(null),
                eq(0L),
                any(),
                eq(List.of()),
                eq(List.of())
        )).thenReturn(expectedResponse);

        // Act
        ConsultarDashboardResponse response = service.consultar();

        // Assert
        assertEquals(expectedResponse, response);
        assertNull(response.notaMediaAvaliacoes());
        verify(pedidoRepository, never()).somarValorTotalExcluindoEstado(any());
        verify(pedidoRepository, never()).findTop5ByOrderByCriadoEmDesc();
        verify(anuncioRepository, never()).somarUnidadesEmEstoque();
        verify(anuncioRepository, never()).agruparPorCategoria();
        verify(avaliacaoRepository, never()).calcularNotaMediaGeral();
    }

    @Test
    @DisplayName("Deve zerar receita e ticket médio mantendo pedidos recentes quando todos pedidos forem cancelados")
    void deveZerarReceitaQuandoTodosPedidosForemCancelados() {

        // Arrange
        Pedido pedidoCancelado = PedidoFactory.get();
        pedidoCancelado.setEstado(CANCELADO);
        List<Pedido> recentes = List.of(pedidoCancelado);

        when(pedidoRepository.countByEstado(CRIADO)).thenReturn(0L);
        when(pedidoRepository.countByEstado(CONFIRMADO)).thenReturn(0L);
        when(pedidoRepository.countByEstado(ENVIADO)).thenReturn(0L);
        when(pedidoRepository.countByEstado(CONCLUIDO)).thenReturn(0L);
        when(pedidoRepository.countByEstado(CANCELADO)).thenReturn(2L);
        when(pedidoRepository.findTop5ByOrderByCriadoEmDesc()).thenReturn(recentes);

        when(anuncioRepository.count()).thenReturn(0L);
        when(anuncioRepository.contarDisponiveis()).thenReturn(0L);
        when(categoriaRepository.countByAtivaTrue()).thenReturn(1L);
        when(avaliacaoRepository.count()).thenReturn(0L);

        when(consultarDashboardMapper.toEstadoPedidoKpiResponse(any(EstadoPedido.class), any(), any()))
                .thenAnswer(invocation -> EstadoPedidoKpiResponse.builder()
                        .estado(invocation.getArgument(0))
                        .quantidade(invocation.getArgument(1))
                        .percentual(invocation.getArgument(2))
                        .build());

        ConsultarDashboardResponse expectedResponse = ConsultarDashboardResponse.builder()
                .receitaTotal(new BigDecimal("0.00"))
                .ticketMedio(new BigDecimal("0.00"))
                .totalPedidos(2L)
                .pedidosConcluidos(0L)
                .pedidosEmAndamento(0L)
                .pedidosCancelados(2L)
                .taxaConclusao(0)
                .totalAnuncios(0L)
                .anunciosDisponiveis(0L)
                .anunciosIndisponiveis(0L)
                .unidadesEmEstoque(0L)
                .totalCategoriasAtivas(1L)
                .notaMediaAvaliacoes(null)
                .totalAvaliacoes(0L)
                .pedidosPorEstado(List.of())
                .anunciosPorCategoria(List.of())
                .pedidosRecentes(List.of())
                .build();

        when(consultarDashboardMapper.toResponse(
                eq(new BigDecimal("0.00")),
                eq(new BigDecimal("0.00")),
                eq(2L),
                eq(0L),
                eq(0L),
                eq(2L),
                eq(0),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(0L),
                eq(1L),
                eq(null),
                eq(0L),
                any(),
                eq(List.of()),
                eq(recentes)
        )).thenReturn(expectedResponse);

        // Act
        ConsultarDashboardResponse response = service.consultar();

        // Assert
        assertEquals(expectedResponse, response);
        verify(pedidoRepository, never()).somarValorTotalExcluindoEstado(any());
        verify(pedidoRepository).findTop5ByOrderByCriadoEmDesc();
    }
}
