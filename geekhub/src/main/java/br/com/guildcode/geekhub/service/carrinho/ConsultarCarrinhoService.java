package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ItemCarrinhoDetalheResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.carrinho.ConsultarCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;

@Service
public class ConsultarCarrinhoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ConsultarCarrinhoMapper consultarCarrinhoMapper;

    public ConsultarCarrinhoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            ItemCarrinhoRepository itemCarrinhoRepository,
            ConsultarCarrinhoMapper consultarCarrinhoMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.consultarCarrinhoMapper = consultarCarrinhoMapper;
    }

    @Transactional(readOnly = true)
    public ConsultarCarrinhoResponse consultar() {

        Usuario comprador = usuarioAutenticadoService.get();
        List<ItemCarrinho> itens = itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(comprador.getId());

        List<ItemCarrinhoDetalheResponse> itensResponse = itens.stream()
                .map(consultarCarrinhoMapper::toItemResponse)
                .toList();

        BigDecimal total = itensResponse.stream()
                .map(ItemCarrinhoDetalheResponse::subtotal)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, HALF_UP);

        return consultarCarrinhoMapper.toResponse(comprador.getId(), itensResponse, total);
    }
}
