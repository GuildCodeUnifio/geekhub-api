package br.com.guildcode.geekhub.openapi.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.EditarCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Categorias", description = "Gerenciamento de categorias de produtos")
public interface CategoriaControllerOpenApi {

    @Operation(summary = "Cadastrar nova categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    })
    CadastrarCategoriaResponse cadastrar(CadastrarCategoriaRequest request);

    @Operation(summary = "Listar categorias de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de categorias retornada com sucesso")
    })
    Page<ListarCategoriaResponse> listar(
            @Parameter(description = "Filtrar por status ativo (true) ou inativo (false)") Boolean ativa,
            Pageable pageable);

    @Operation(summary = "Detalhar categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    DetalharCategoriaResponse detalhar(Long id);

    @Operation(summary = "Editar categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    })
    EditarCategoriaResponse editar(Long id, EditarCategoriaRequest request);

    @Operation(summary = "Remover categoria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Categoria possui anúncios vinculados")
    })
    void remover(Long id);
}
