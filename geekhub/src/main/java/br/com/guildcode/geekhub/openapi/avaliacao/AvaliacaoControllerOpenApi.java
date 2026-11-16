package br.com.guildcode.geekhub.openapi.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.DetalharAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.EditarAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.ListarAvaliacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Avaliações", description = "Registro e gerenciamento de avaliações de compras concluídas")
public interface AvaliacaoControllerOpenApi {

    @Operation(summary = "Registrar avaliação de uma compra concluída")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou pedido não concluído"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Apenas o comprador responsável pelo pedido pode avaliá-lo"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "409", description = "Este pedido já possui avaliação registrada")
    })
    CadastrarAvaliacaoResponse cadastrar(CadastrarAvaliacaoRequest request);

    @Operation(summary = "Listar avaliações paginadas com filtro opcional por vendedor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de avaliações retornada com sucesso")
    })
    Page<ListarAvaliacaoResponse> listar(
            @Parameter(description = "Filtro opcional pelo ID do vendedor") Long vendedorId,
            Pageable pageable);

    @Operation(summary = "Detalhar avaliação por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    DetalharAvaliacaoResponse detalhar(Long id);

    @Operation(summary = "Editar avaliação existente (apenas autor)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de avaliação inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Apenas o autor pode editar esta avaliação"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    EditarAvaliacaoResponse editar(Long id, EditarAvaliacaoRequest request);

    @Operation(summary = "Remover avaliação por ID (autor ou ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Avaliação removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para remover esta avaliação"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")
    })
    void remover(Long id);
}
