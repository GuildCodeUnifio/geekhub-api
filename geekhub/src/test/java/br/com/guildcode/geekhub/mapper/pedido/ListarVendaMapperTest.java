package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarVendaMapperTest {

    private final ListarVendaMapper mapper = new ListarVendaMapper();

    @Test
    @DisplayName("Deve mapear ListarVendaResponse a partir do Pedido")
    void deveMapearListarVendaResponseCorretamente() {
        // Arrange
        Pedido entity = PedidoFactory.get();

        // Act
        ListarVendaResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getCompradorNome(), response.compradorNome());
        assertEquals(entity.getValorTotal(), response.valorTotal());
        assertEquals(entity.getEstado(), response.estado());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
    }
}
