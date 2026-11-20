package br.com.guildcode.geekhub.openapi.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.controller.pedido.dto.response.AtualizarEstadoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Pedidos", description = "Criação, consulta e gerenciamento de pedidos")
public interface PedidoControllerOpenApi {

    @Operation(summary = "Confirmar criação de pedido a partir do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado e estoque descontado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Carrinho de compras vazio"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "409", description = "Anúncio indisponível, estoque insuficiente ou itens de vendedores diferentes")
    })
    CriarPedidoResponse criar();

    @Operation(summary = "Listar compras realizadas pelo usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de compras do comprador retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    Page<ListarCompraResponse> listarCompras(
            @Parameter(description = "Filtro opcional pelo estado atual do pedido") EstadoPedido estado,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Listar vendas recebidas contendo anúncios do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de vendas do vendedor retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    Page<ListarVendaResponse> listarVendas(
            @Parameter(description = "Filtro opcional pelo estado atual do pedido") EstadoPedido estado,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Listar todos os pedidos da plataforma (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página administrativa de pedidos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    Page<ListarPedidoAdminResponse> listarTodos(@ParameterObject Pageable pageable);

    @Operation(summary = "Detalhar pedido por ID com itens congelados e histórico cronológico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes do pedido retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para visualizar o pedido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    DetalharPedidoResponse detalhar(@Parameter(description = "ID do pedido") Long id);

    @Operation(summary = "Atualizar estado do pedido (avanço de entrega ou cancelamento com estorno de estoque)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado do pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou transição de estado não permitida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para atualizar o estado deste pedido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    AtualizarEstadoPedidoResponse atualizarEstado(
            @Parameter(description = "ID do pedido") Long id,
            AtualizarEstadoPedidoRequest request);

    @Operation(summary = "Remover pedido por ID (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível remover pedido com avaliação vinculada")
    })
    void remover(@Parameter(description = "ID do pedido") Long id);
}
