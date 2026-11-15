package br.com.guildcode.geekhub.openapi.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Pedidos", description = "Criação, consulta e gerenciamento de pedidos")
public interface PedidoControllerOpenApi {

    @Operation(summary = "Confirmar criação de pedido a partir do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado e estoque descontado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Carrinho de compras vazio"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "409", description = "Anúncio indisponível ou estoque insuficiente")
    })
    CriarPedidoResponse criar();
}
