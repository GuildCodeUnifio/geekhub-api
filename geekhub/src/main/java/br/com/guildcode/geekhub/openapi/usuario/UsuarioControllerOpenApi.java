package br.com.guildcode.geekhub.openapi.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.response.EditarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.response.ListarUsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Usuários", description = "Cadastro e gerenciamento de usuários")
public interface UsuarioControllerOpenApi {

    @Operation(summary = "Cadastrar novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    CadastrarUsuarioResponse cadastrar(CadastrarUsuarioRequest request);

    @Operation(summary = "Listar usuários de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    Page<ListarUsuarioResponse> listar(Pageable pageable);

    @Operation(summary = "Detalhar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    DetalharUsuarioResponse detalhar(Long id);

    @Operation(summary = "Editar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    EditarUsuarioResponse editar(Long id, EditarUsuarioRequest request);

    @Operation(summary = "Remover usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    void remover(Long id);
}
