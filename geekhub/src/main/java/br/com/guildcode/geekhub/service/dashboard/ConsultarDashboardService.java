package br.com.guildcode.geekhub.service.dashboard;

import br.com.guildcode.geekhub.controller.dashboard.dto.response.CategoriaKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.EstadoPedidoKpiResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.mapper.dashboard.ConsultarDashboardMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.anuncio.ResumoCategoriaAnuncioProjection;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

@Service
public class ConsultarDashboardService {

    private final PedidoRepository pedidoRepository;
    private final AnuncioRepository anuncioRepository;
    private final CategoriaRepository categoriaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ConsultarDashboardMapper consultarDashboardMapper;

    public ConsultarDashboardService(
            PedidoRepository pedidoRepository,
            AnuncioRepository anuncioRepository,
            CategoriaRepository categoriaRepository,
            AvaliacaoRepository avaliacaoRepository,
            ConsultarDashboardMapper consultarDashboardMapper) {

        this.pedidoRepository = pedidoRepository;
        this.anuncioRepository = anuncioRepository;
        this.categoriaRepository = categoriaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.consultarDashboardMapper = consultarDashboardMapper;
    }

    @Transactional(readOnly = true)
    public ConsultarDashboardResponse consultar() {

        Map<EstadoPedido, Long> contagemPorEstado = contarPedidosPorEstado();
        Long totalPedidos = contagemPorEstado.values().stream().reduce(0L, Long::sum);
        Long pedidosConcluidos = contagemPorEstado.get(CONCLUIDO);
        Long pedidosCancelados = contagemPorEstado.get(CANCELADO);
        Long pedidosEmAndamento = contagemPorEstado.get(CRIADO)
                + contagemPorEstado.get(CONFIRMADO)
                + contagemPorEstado.get(ENVIADO);
        Long pedidosValidos = totalPedidos - pedidosCancelados;

        BigDecimal receitaTotal = calcularReceitaTotal(pedidosValidos);
        BigDecimal ticketMedio = calcularTicketMedio(receitaTotal, pedidosValidos);
        Integer taxaConclusao = calcularPercentual(pedidosConcluidos, totalPedidos);

        List<EstadoPedidoKpiResponse> pedidosPorEstado = Arrays.stream(EstadoPedido.values())
                .map(estado -> consultarDashboardMapper.toEstadoPedidoKpiResponse(
                        estado,
                        contagemPorEstado.get(estado),
                        calcularPercentual(contagemPorEstado.get(estado), totalPedidos)
                ))
                .toList();

        List<Pedido> pedidosRecentes = totalPedidos > 0L
                ? pedidoRepository.findTop5ByOrderByCriadoEmDesc()
                : List.of();

        Long totalAnuncios = anuncioRepository.count();
        Long anunciosDisponiveis = anuncioRepository.contarDisponiveis();
        Long anunciosIndisponiveis = totalAnuncios - anunciosDisponiveis;
        Long unidadesEmEstoque = totalAnuncios > 0L
                ? anuncioRepository.somarUnidadesEmEstoque()
                : 0L;
        Long totalCategoriasAtivas = categoriaRepository.countByAtivaTrue();

        List<ResumoCategoriaAnuncioProjection> categoriasAgrupadas = totalAnuncios > 0L
                ? anuncioRepository.agruparPorCategoria()
                : List.of();
        List<CategoriaKpiResponse> anunciosPorCategoria = categoriasAgrupadas.stream()
                .map(categoria -> consultarDashboardMapper.toCategoriaKpiResponse(
                        categoria,
                        calcularPercentual(categoria.getQuantidadeAnuncios(), totalAnuncios)
                ))
                .toList();

        Long totalAvaliacoes = avaliacaoRepository.count();
        Double notaMediaAvaliacoes = calcularNotaMedia(totalAvaliacoes);

        return consultarDashboardMapper.toResponse(
                receitaTotal,
                ticketMedio,
                totalPedidos,
                pedidosConcluidos,
                pedidosEmAndamento,
                pedidosCancelados,
                taxaConclusao,
                totalAnuncios,
                anunciosDisponiveis,
                anunciosIndisponiveis,
                unidadesEmEstoque,
                totalCategoriasAtivas,
                notaMediaAvaliacoes,
                totalAvaliacoes,
                pedidosPorEstado,
                anunciosPorCategoria,
                pedidosRecentes
        );
    }

    private Map<EstadoPedido, Long> contarPedidosPorEstado() {

        Map<EstadoPedido, Long> contagem = new EnumMap<>(EstadoPedido.class);
        for (EstadoPedido estado : EstadoPedido.values()) {
            contagem.put(estado, pedidoRepository.countByEstado(estado));
        }

        return contagem;
    }

    private BigDecimal calcularReceitaTotal(Long pedidosValidos) {

        if (pedidosValidos.equals(0L)) {
            return ZERO.setScale(2, HALF_UP);
        }

        return pedidoRepository.somarValorTotalExcluindoEstado(CANCELADO).setScale(2, HALF_UP);
    }

    private BigDecimal calcularTicketMedio(BigDecimal receitaTotal, Long pedidosValidos) {

        if (pedidosValidos.equals(0L)) {
            return ZERO.setScale(2, HALF_UP);
        }

        return receitaTotal.divide(BigDecimal.valueOf(pedidosValidos), 2, HALF_UP);
    }

    private Integer calcularPercentual(Long parte, Long total) {

        if (total.equals(0L)) {
            return 0;
        }

        return BigDecimal.valueOf(parte)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 0, HALF_UP)
                .intValue();
    }

    private Double calcularNotaMedia(Long totalAvaliacoes) {

        if (totalAvaliacoes.equals(0L)) {
            return null;
        }

        Double notaMediaBruta = avaliacaoRepository.calcularNotaMediaGeral();

        return BigDecimal.valueOf(notaMediaBruta).setScale(1, HALF_UP).doubleValue();
    }
}
