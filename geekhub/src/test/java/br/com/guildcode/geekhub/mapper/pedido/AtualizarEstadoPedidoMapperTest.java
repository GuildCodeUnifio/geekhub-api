package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.AtualizarEstadoPedidoResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AtualizarEstadoPedidoMapperTest {

    private final AtualizarEstadoPedidoMapper mapper = new AtualizarEstadoPedidoMapper();

    @Test
    @DisplayName("Deve mapear pedido atualizado para AtualizarEstadoPedidoResponse com histórico cronológico")
    void deveMapearParaAtualizarEstadoPedidoResponse() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        LocalDateTime momentoConfirmacao = LocalDateTime.of(2026, 11, 15, 21, 0);
        pedido.registrarEvento(CONFIRMADO, momentoConfirmacao);

        // Act
        AtualizarEstadoPedidoResponse response = mapper.toResponse(pedido);

        // Assert
        assertEquals(pedido.getId(), response.id());
        assertEquals(CONFIRMADO, response.estado());
        assertEquals(FALSE, response.avaliado());
        assertEquals(2, response.historico().size());
        assertEquals(CRIADO, response.historico().get(0).estado());
        assertEquals(CONFIRMADO, response.historico().get(1).estado());
        assertEquals(momentoConfirmacao, response.historico().get(1).ocorridoEm());
    }
}
