package br.com.guildcode.geekhub.openapi.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.DetalharItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.EditarItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras do comprador autenticado")
public interface CarrinhoControllerOpenApi {

    @Operation(summary = "Adicionar produto ao carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado ou incrementado no carrinho com sucesso"),
            @ApiResponse(responseCode = "400", description = "Anúncio indisponível, próprio ou quantidade acima do estoque"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Anúncio não encontrado")
    })
    AdicionarItemCarrinhoResponse adicionarItem(AdicionarItemCarrinhoRequest request);

    @Operation(summary = "Consultar carrinho consolidado com subtotais e total")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho consultado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    ConsultarCarrinhoResponse consultar();

    @Operation(summary = "Listar itens do carrinho de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de itens do carrinho retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    Page<ListarItemCarrinhoResponse> listarItens(@ParameterObject Pageable pageable);

    @Operation(summary = "Detalhar item específico do carrinho por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item do carrinho retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Item pertence a outro comprador"),
            @ApiResponse(responseCode = "404", description = "Item do carrinho não encontrado")
    })
    DetalharItemCarrinhoResponse detalharItem(@Parameter(description = "ID do item do carrinho") Long id);

    @Operation(summary = "Atualizar quantidade de um item do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantidade do item atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida, anúncio indisponível ou acima do estoque"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Item pertence a outro comprador"),
            @ApiResponse(responseCode = "404", description = "Item do carrinho não encontrado")
    })
    EditarItemCarrinhoResponse editarItem(
            @Parameter(description = "ID do item do carrinho") Long id,
            EditarItemCarrinhoRequest request);

    @Operation(summary = "Remover item específico do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido do carrinho com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Item pertence a outro comprador"),
            @ApiResponse(responseCode = "404", description = "Item do carrinho não encontrado")
    })
    void removerItem(@Parameter(description = "ID do item do carrinho") Long id);

    @Operation(summary = "Esvaziar todos os itens do carrinho do comprador autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrinho esvaziado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    void limpar();
}
