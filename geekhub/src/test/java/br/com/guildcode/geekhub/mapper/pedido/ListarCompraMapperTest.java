package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarCompraMapperTest {

    private final ListarCompraMapper mapper = new ListarCompraMapper();

    @Test
    @DisplayName("Deve mapear ListarCompraResponse com resumo financeiro e quantidade total de itens")
    void deveMapearListarCompraResponseCorretamente() {
        // Arrange
        Pedido entity = PedidoFactory.get();

        // Act
        ListarCompraResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getValorTotal(), response.valorTotal());
        assertEquals(entity.getEstado(), response.estado());
        assertEquals(entity.getAvaliado(), response.avaliado());
        assertEquals(1, response.quantidadeItens());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
    }
}
