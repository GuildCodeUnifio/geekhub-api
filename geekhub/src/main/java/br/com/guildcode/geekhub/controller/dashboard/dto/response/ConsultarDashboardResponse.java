package br.com.guildcode.geekhub.controller.dashboard.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ConsultarDashboardResponse(

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
        List<PedidoRecenteDashboardResponse> pedidosRecentes
) {}
