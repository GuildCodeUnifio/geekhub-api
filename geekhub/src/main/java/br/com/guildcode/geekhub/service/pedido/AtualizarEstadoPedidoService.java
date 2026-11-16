package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.controller.pedido.dto.response.AtualizarEstadoPedidoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.pedido.AtualizarEstadoPedidoMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.anuncio.BuscarAnuncioService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.pedido.validator.AcessoPedidoValidatorService;
import br.com.guildcode.geekhub.service.pedido.validator.TransicaoEstadoPedidoValidatorService;
import br.com.guildcode.geekhub.validator.pedido.AtualizarEstadoPedidoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;

@Service
public class AtualizarEstadoPedidoService {

    private final AtualizarEstadoPedidoValidator atualizarEstadoPedidoValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarPedidoService buscarPedidoService;
    private final AcessoPedidoValidatorService acessoPedidoValidatorService;
    private final TransicaoEstadoPedidoValidatorService transicaoEstadoPedidoValidatorService;
    private final BuscarAnuncioService buscarAnuncioService;
    private final AnuncioRepository anuncioRepository;
    private final PedidoRepository pedidoRepository;
    private final AtualizarEstadoPedidoMapper atualizarEstadoPedidoMapper;

    public AtualizarEstadoPedidoService(
            AtualizarEstadoPedidoValidator atualizarEstadoPedidoValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarPedidoService buscarPedidoService,
            AcessoPedidoValidatorService acessoPedidoValidatorService,
            TransicaoEstadoPedidoValidatorService transicaoEstadoPedidoValidatorService,
            BuscarAnuncioService buscarAnuncioService,
            AnuncioRepository anuncioRepository,
            PedidoRepository pedidoRepository,
            AtualizarEstadoPedidoMapper atualizarEstadoPedidoMapper) {

        this.atualizarEstadoPedidoValidator = atualizarEstadoPedidoValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarPedidoService = buscarPedidoService;
        this.acessoPedidoValidatorService = acessoPedidoValidatorService;
        this.transicaoEstadoPedidoValidatorService = transicaoEstadoPedidoValidatorService;
        this.buscarAnuncioService = buscarAnuncioService;
        this.anuncioRepository = anuncioRepository;
        this.pedidoRepository = pedidoRepository;
        this.atualizarEstadoPedidoMapper = atualizarEstadoPedidoMapper;
    }

    @Transactional
    public AtualizarEstadoPedidoResponse atualizar(Long id, AtualizarEstadoPedidoRequest request) {

        atualizarEstadoPedidoValidator.validarDadosAtualizacao(request);

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        Pedido pedido = buscarPedidoService.buscarPorId(id);

        acessoPedidoValidatorService.validarAtualizacaoEstado(pedido, usuarioAutenticado, request.estado());
        transicaoEstadoPedidoValidatorService.validar(pedido.getEstado(), request.estado());

        if (CANCELADO.equals(request.estado())) {
            estornarEstoque(pedido);
        }

        pedido.registrarEvento(request.estado(), LocalDateTime.now());
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return atualizarEstadoPedidoMapper.toResponse(pedidoSalvo);
    }

    private void estornarEstoque(Pedido pedido) {

        for (ItemPedido item : pedido.getItens()) {
            Anuncio anuncio = buscarAnuncioService.buscarPorId(item.getAnuncioId());
            anuncio.estornarEstoque(item.getQuantidade());
            anuncioRepository.save(anuncio);
        }
    }
}
