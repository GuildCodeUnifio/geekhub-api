package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ItemPedidoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.pedido.EventoPedido;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

@Component
public class CriarPedidoMapper {

    public ItemPedido toItemPedido(ItemCarrinho itemCarrinho) {

        Anuncio anuncio = itemCarrinho.getAnuncio();

        return ItemPedido.builder()
                .anuncioId(anuncio.getId())
                .vendedorId(anuncio.getVendedor().getId())
                .nomeProduto(anuncio.getNome())
                .imagemProduto(anuncio.obterImagemPrincipal())
                .precoUnitario(anuncio.getPreco())
                .quantidade(itemCarrinho.getQuantidade())
                .subtotal(itemCarrinho.calcularSubtotal())
                .build();
    }

    public Pedido toEntity(Usuario comprador, List<ItemPedido> itens, LocalDateTime agora) {

        return Pedido.builder()
                .comprador(comprador)
                .compradorNome(comprador.getNome())
                .compradorEmail(comprador.getEmail())
                .itens(new ArrayList<>(itens))
                .avaliado(FALSE)
                .criadoEm(agora)
                .build();
    }

    public CriarPedidoResponse toResponse(Pedido entity) {

        List<ItemPedidoResponse> itensResponse = entity.getItens().stream()
                .map(this::toItemResponse)
                .toList();

        List<EventoPedidoResponse> historicoResponse = entity.getHistorico().stream()
                .map(this::toEventoResponse)
                .toList();

        return CriarPedidoResponse.builder()
                .id(entity.getId())
                .compradorId(entity.getComprador().getId())
                .compradorNome(entity.getCompradorNome())
                .compradorEmail(entity.getCompradorEmail())
                .itens(itensResponse)
                .valorTotal(entity.getValorTotal())
                .estado(entity.getEstado())
                .historico(historicoResponse)
                .avaliado(entity.getAvaliado())
                .criadoEm(entity.getCriadoEm())
                .build();
    }

    private ItemPedidoResponse toItemResponse(ItemPedido item) {

        return ItemPedidoResponse.builder()
                .anuncioId(item.getAnuncioId())
                .vendedorId(item.getVendedorId())
                .nomeProduto(item.getNomeProduto())
                .imagemProduto(item.getImagemProduto())
                .precoUnitario(item.getPrecoUnitario())
                .quantidade(item.getQuantidade())
                .subtotal(item.getSubtotal())
                .build();
    }

    private EventoPedidoResponse toEventoResponse(EventoPedido evento) {

        return EventoPedidoResponse.builder()
                .estado(evento.getEstado())
                .ocorridoEm(evento.getOcorridoEm())
                .build();
    }
}
