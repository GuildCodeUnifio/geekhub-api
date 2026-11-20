package br.com.guildcode.geekhub.mapper.dashboard;

import br.com.guildcode.geekhub.controller.dashboard.dto.response.CategoriaKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.EstadoPedidoKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.PedidoRecenteDashboardResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.repository.anuncio.ResumoCategoriaAnuncioProjection;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ConsultarDashboardMapper {

    public EstadoPedidoKpiResponse toEstadoPedidoKpiResponse(
            EstadoPedido estado,
            Long quantidade,
            Integer percentual) {

        return EstadoPedidoKpiResponse.builder()
                .estado(estado)
                .quantidade(quantidade)
                .percentual(percentual)
                .build();
    }

    public CategoriaKpiResponse toCategoriaKpiResponse(
            ResumoCategoriaAnuncioProjection categoria,
            Integer percentual) {

        return CategoriaKpiResponse.builder()
                .nome(categoria.getCategoriaNome())
                .quantidadeAnuncios(categoria.getQuantidadeAnuncios())
                .estoqueTotal(categoria.getEstoqueTotal())
                .percentual(percentual)
                .build();
    }

    public ConsultarDashboardResponse toResponse(
            BigDecimal receitaTotal,
            BigDecimal ticketMedio,
            Long totalPedidos,
            Long pedidosConcluidos,
            Long pedidosEmAndamento,
            Long pedidosCancelados,
            Integer taxaConclusao,
            Long totalAnuncios,
            Long anunciosDisponiveis,
            Long anunciosIndisponiveis,
            Long unidadesEmEstoque,
            Long totalCategoriasAtivas,
            Double notaMediaAvaliacoes,
            Long totalAvaliacoes,
            List<EstadoPedidoKpiResponse> pedidosPorEstado,
            List<CategoriaKpiResponse> anunciosPorCategoria,
            List<Pedido> pedidosRecentes) {

        return ConsultarDashboardResponse.builder()
                .receitaTotal(receitaTotal)
                .ticketMedio(ticketMedio)
                .totalPedidos(totalPedidos)
                .pedidosConcluidos(pedidosConcluidos)
                .pedidosEmAndamento(pedidosEmAndamento)
                .pedidosCancelados(pedidosCancelados)
                .taxaConclusao(taxaConclusao)
                .totalAnuncios(totalAnuncios)
                .anunciosDisponiveis(anunciosDisponiveis)
                .anunciosIndisponiveis(anunciosIndisponiveis)
                .unidadesEmEstoque(unidadesEmEstoque)
                .totalCategoriasAtivas(totalCategoriasAtivas)
                .notaMediaAvaliacoes(notaMediaAvaliacoes)
                .totalAvaliacoes(totalAvaliacoes)
                .pedidosPorEstado(pedidosPorEstado)
                .anunciosPorCategoria(anunciosPorCategoria)
                .pedidosRecentes(pedidosRecentes.stream().map(this::toPedidoRecenteResponse).toList())
                .build();
    }

    private PedidoRecenteDashboardResponse toPedidoRecenteResponse(Pedido entity) {

        return PedidoRecenteDashboardResponse.builder()
                .id(entity.getId())
                .compradorNome(entity.getCompradorNome())
                .estado(entity.getEstado())
                .valorTotal(entity.getValorTotal())
                .quantidadeItens(entity.calcularQuantidadeItens())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
