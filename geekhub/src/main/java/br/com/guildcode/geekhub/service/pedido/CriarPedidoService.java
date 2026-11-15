package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.pedido.CriarPedidoMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.pedido.validator.CarrinhoNaoVazioValidatorService;
import br.com.guildcode.geekhub.service.pedido.validator.EstoqueDisponivelPedidoValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;

@Service
public class CriarPedidoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final CarrinhoNaoVazioValidatorService carrinhoNaoVazioValidatorService;
    private final EstoqueDisponivelPedidoValidatorService estoqueDisponivelPedidoValidatorService;
    private final AnuncioRepository anuncioRepository;
    private final PedidoRepository pedidoRepository;
    private final CriarPedidoMapper criarPedidoMapper;

    public CriarPedidoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            ItemCarrinhoRepository itemCarrinhoRepository,
            CarrinhoNaoVazioValidatorService carrinhoNaoVazioValidatorService,
            EstoqueDisponivelPedidoValidatorService estoqueDisponivelPedidoValidatorService,
            AnuncioRepository anuncioRepository,
            PedidoRepository pedidoRepository,
            CriarPedidoMapper criarPedidoMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.carrinhoNaoVazioValidatorService = carrinhoNaoVazioValidatorService;
        this.estoqueDisponivelPedidoValidatorService = estoqueDisponivelPedidoValidatorService;
        this.anuncioRepository = anuncioRepository;
        this.pedidoRepository = pedidoRepository;
        this.criarPedidoMapper = criarPedidoMapper;
    }

    @Transactional
    public CriarPedidoResponse criar() {

        Usuario comprador = usuarioAutenticadoService.get();
        List<ItemCarrinho> itensCarrinho =
                itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(comprador.getId());

        carrinhoNaoVazioValidatorService.validar(itensCarrinho);
        estoqueDisponivelPedidoValidatorService.validar(itensCarrinho);

        LocalDateTime agora = LocalDateTime.now();
        List<ItemPedido> itensPedido = itensCarrinho.stream()
                .map(criarPedidoMapper::toItemPedido)
                .toList();

        baixarEstoque(itensCarrinho);

        Pedido pedido = criarPedidoMapper.toEntity(comprador, itensPedido, agora);
        pedido.calcularValorTotal();
        pedido.registrarEvento(CRIADO, agora);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        itemCarrinhoRepository.deleteByCompradorId(comprador.getId());

        return criarPedidoMapper.toResponse(pedidoSalvo);
    }

    private void baixarEstoque(List<ItemCarrinho> itensCarrinho) {

        for (ItemCarrinho item : itensCarrinho) {
            Anuncio anuncio = item.getAnuncio();
            anuncio.descontarEstoque(item.getQuantidade());
            anuncioRepository.save(anuncio);
        }
    }
}
