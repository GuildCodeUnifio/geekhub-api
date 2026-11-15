package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DetalharPedidoMapperTest {

    private final DetalharPedidoMapper mapper = new DetalharPedidoMapper();

    @Test
    @DisplayName("Deve mapear DetalharPedidoResponse completo com itens congelados e histórico cronológico")
    void deveMapearDetalharPedidoResponseCorretamente() {
        // Arrange
        Pedido entity = PedidoFactory.get();

        // Act
        DetalharPedidoResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getComprador().getId(), response.compradorId());
        assertEquals(entity.getCompradorNome(), response.compradorNome());
        assertEquals(entity.getCompradorEmail(), response.compradorEmail());
        assertEquals(entity.getValorTotal(), response.valorTotal());
        assertEquals(entity.getEstado(), response.estado());
        assertEquals(entity.getAvaliado(), response.avaliado());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
        assertEquals(1, response.itens().size());
        assertEquals(entity.getItens().getFirst().getAnuncioId(), response.itens().getFirst().anuncioId());
        assertEquals(entity.getItens().getFirst().getVendedorId(), response.itens().getFirst().vendedorId());
        assertEquals(entity.getItens().getFirst().getNomeProduto(), response.itens().getFirst().nomeProduto());
        assertEquals(entity.getItens().getFirst().getImagemProduto(), response.itens().getFirst().imagemProduto());
        assertEquals(entity.getItens().getFirst().getPrecoUnitario(), response.itens().getFirst().precoUnitario());
        assertEquals(entity.getItens().getFirst().getQuantidade(), response.itens().getFirst().quantidade());
        assertEquals(entity.getItens().getFirst().getSubtotal(), response.itens().getFirst().subtotal());
        assertEquals(1, response.historico().size());
        assertEquals(CRIADO, response.historico().getFirst().estado());
        assertEquals(entity.getCriadoEm(), response.historico().getFirst().ocorridoEm());
    }
}
