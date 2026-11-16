package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.PerfilVendedorResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.usuario.PerfilVendedorMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static java.math.RoundingMode.HALF_UP;

@Service
public class PerfilVendedorService {

    private final BuscarUsuarioService buscarUsuarioService;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AnuncioRepository anuncioRepository;
    private final PedidoRepository pedidoRepository;
    private final PerfilVendedorMapper perfilVendedorMapper;

    public PerfilVendedorService(
            BuscarUsuarioService buscarUsuarioService,
            AvaliacaoRepository avaliacaoRepository,
            AnuncioRepository anuncioRepository,
            PedidoRepository pedidoRepository,
            PerfilVendedorMapper perfilVendedorMapper) {

        this.buscarUsuarioService = buscarUsuarioService;
        this.avaliacaoRepository = avaliacaoRepository;
        this.anuncioRepository = anuncioRepository;
        this.pedidoRepository = pedidoRepository;
        this.perfilVendedorMapper = perfilVendedorMapper;
    }

    @Transactional(readOnly = true)
    public PerfilVendedorResponse consultar(Long id) {

        Usuario vendedor = buscarUsuarioService.buscarPorId(id);
        Long vendedorId = vendedor.getId();

        Long quantidadeAvaliacoes = avaliacaoRepository.contarPorVendedorId(vendedorId);
        Double notaMedia = calcularNotaMedia(vendedorId, quantidadeAvaliacoes);
        List<Avaliacao> avaliacoes = quantidadeAvaliacoes > 0
                ? avaliacaoRepository.findByVendedorIdOrderByCriadoEmDesc(vendedorId)
                : List.of();

        Long quantidadeProdutos = anuncioRepository.contarDisponiveisPorVendedorId(vendedorId);
        List<Anuncio> anuncios = quantidadeProdutos > 0
                ? anuncioRepository.listarDisponiveisPorVendedorId(vendedorId)
                : List.of();

        Long quantidadeVendas = pedidoRepository.contarPorVendedorIdEEstado(vendedorId, CONCLUIDO);

        return perfilVendedorMapper.toResponse(
                vendedor,
                notaMedia,
                quantidadeAvaliacoes,
                quantidadeProdutos,
                quantidadeVendas,
                avaliacoes,
                anuncios
        );
    }

    private Double calcularNotaMedia(Long vendedorId, Long quantidadeAvaliacoes) {

        if (quantidadeAvaliacoes == 0) {
            return null;
        }

        Double notaMediaBruta = avaliacaoRepository.calcularNotaMediaPorVendedorId(vendedorId);

        return BigDecimal.valueOf(notaMediaBruta).setScale(2, HALF_UP).doubleValue();
    }
}
