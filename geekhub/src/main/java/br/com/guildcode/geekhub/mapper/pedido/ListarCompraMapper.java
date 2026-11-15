package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.stereotype.Component;

@Component
public class ListarCompraMapper {

    public ListarCompraResponse toResponse(Pedido entity) {

        return ListarCompraResponse.builder()
                .id(entity.getId())
                .valorTotal(entity.getValorTotal())
                .estado(entity.getEstado())
                .avaliado(entity.getAvaliado())
                .quantidadeItens(entity.calcularQuantidadeItens())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
