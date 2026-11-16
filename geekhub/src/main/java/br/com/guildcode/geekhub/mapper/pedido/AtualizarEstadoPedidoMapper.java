package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.AtualizarEstadoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.domain.pedido.EventoPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AtualizarEstadoPedidoMapper {

    public AtualizarEstadoPedidoResponse toResponse(Pedido entity) {

        List<EventoPedidoResponse> historicoResponse = entity.getHistorico().stream()
                .map(this::toEventoResponse)
                .toList();

        return AtualizarEstadoPedidoResponse.builder()
                .id(entity.getId())
                .estado(entity.getEstado())
                .historico(historicoResponse)
                .avaliado(entity.getAvaliado())
                .build();
    }

    private EventoPedidoResponse toEventoResponse(EventoPedido evento) {

        return EventoPedidoResponse.builder()
                .estado(evento.getEstado())
                .ocorridoEm(evento.getOcorridoEm())
                .build();
    }
}
