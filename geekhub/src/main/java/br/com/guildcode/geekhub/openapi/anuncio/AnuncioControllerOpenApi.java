package br.com.guildcode.geekhub.openapi.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Anúncios", description = "Cadastro, edição e remoção de anúncios de produtos")
public interface AnuncioControllerOpenApi {

    @Operation(summary = "Cadastrar novo anúncio")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Anúncio cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição ou categoria desativada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    CadastrarAnuncioResponse cadastrar(CadastrarAnuncioRequest request);

    @Operation(summary = "Editar anúncio próprio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anúncio atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição ou categoria desativada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é o proprietário do anúncio"),
            @ApiResponse(responseCode = "404", description = "Anúncio ou categoria não encontrado")
    })
    EditarAnuncioResponse editar(Long id, EditarAnuncioRequest request);

    @Operation(summary = "Remover anúncio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Anúncio removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para remover o anúncio"),
            @ApiResponse(responseCode = "404", description = "Anúncio não encontrado"),
            @ApiResponse(responseCode = "409", description = "Anúncio possui pedidos vinculados")
    })
    void remover(Long id);
}
