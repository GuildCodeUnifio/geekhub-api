package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ItemPedidoResponse;
import br.com.guildcode.geekhub.domain.pedido.EventoPedido;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetalharPedidoMapper {

    public DetalharPedidoResponse toResponse(Pedido entity) {

        List<ItemPedidoResponse> itensResponse = entity.getItens().stream()
                .map(this::toItemResponse)
                .toList();

        List<EventoPedidoResponse> historicoResponse = entity.getHistorico().stream()
                .map(this::toEventoResponse)
                .toList();

        return DetalharPedidoResponse.builder()
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
