package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.stereotype.Component;

@Component
public class ListarVendaMapper {

    public ListarVendaResponse toResponse(Pedido entity) {

        return ListarVendaResponse.builder()
                .id(entity.getId())
                .compradorNome(entity.getCompradorNome())
                .valorTotal(entity.getValorTotal())
                .estado(entity.getEstado())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
