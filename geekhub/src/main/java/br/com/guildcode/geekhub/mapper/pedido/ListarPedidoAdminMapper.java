package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.stereotype.Component;

@Component
public class ListarPedidoAdminMapper {

    public ListarPedidoAdminResponse toResponse(Pedido entity) {

        return ListarPedidoAdminResponse.builder()
                .id(entity.getId())
                .compradorId(entity.getComprador().getId())
                .compradorNome(entity.getCompradorNome())
                .compradorEmail(entity.getCompradorEmail())
                .valorTotal(entity.getValorTotal())
                .estado(entity.getEstado())
                .avaliado(entity.getAvaliado())
                .quantidadeItens(entity.calcularQuantidadeItens())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
