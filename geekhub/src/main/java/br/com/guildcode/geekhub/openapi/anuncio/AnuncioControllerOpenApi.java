package br.com.guildcode.geekhub.openapi.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Anúncios", description = "Cadastro, consulta, edição e remoção de anúncios de produtos")
public interface AnuncioControllerOpenApi {

    @Operation(summary = "Cadastrar novo anúncio")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Anúncio cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição ou categoria desativada"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    CadastrarAnuncioResponse cadastrar(CadastrarAnuncioRequest request);

    @Operation(summary = "Detalhar anúncio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes do anúncio retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Anúncio não encontrado")
    })
    DetalharAnuncioResponse detalhar(Long id);

    @Operation(summary = "Listar anúncios próprios do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de anúncios do vendedor retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    Page<ListarMeuAnuncioResponse> listarMeus(Pageable pageable);

    @Operation(summary = "Listar todos os anúncios da plataforma (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página administrativa de anúncios retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    Page<ListarAnuncioAdminResponse> listarAdmin(Pageable pageable);

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
