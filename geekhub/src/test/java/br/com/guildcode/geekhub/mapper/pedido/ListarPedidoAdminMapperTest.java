package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarPedidoAdminMapperTest {

    private final ListarPedidoAdminMapper mapper = new ListarPedidoAdminMapper();

    @Test
    @DisplayName("Deve mapear ListarPedidoAdminResponse com dados do comprador, totais e quantidade de itens")
    void deveMapearListarPedidoAdminResponseCorretamente() {
        // Arrange
        Pedido entity = PedidoFactory.get();

        // Act
        ListarPedidoAdminResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getComprador().getId(), response.compradorId());
        assertEquals(entity.getCompradorNome(), response.compradorNome());
        assertEquals(entity.getCompradorEmail(), response.compradorEmail());
        assertEquals(entity.getValorTotal(), response.valorTotal());
        assertEquals(entity.getEstado(), response.estado());
        assertEquals(entity.getAvaliado(), response.avaliado());
        assertEquals(1, response.quantidadeItens());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
    }
}
