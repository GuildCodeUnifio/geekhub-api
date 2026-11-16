package br.com.guildcode.geekhub.openapi.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AtualizarPermissaoUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.EditarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.ListarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.PerfilVendedorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Usuários", description = "Cadastro e gerenciamento de usuários")
public interface UsuarioControllerOpenApi {

    @Operation(summary = "Cadastrar novo usuário")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    CadastrarUsuarioResponse cadastrar(CadastrarUsuarioRequest request);

    @Operation(summary = "Listar usuários de forma paginada (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    Page<ListarUsuarioResponse> listar(@ParameterObject Pageable pageable);

    @Operation(summary = "Detalhar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    DetalharUsuarioResponse detalhar(@Parameter(description = "ID do usuário") Long id);

    @Operation(summary = "Editar usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    EditarUsuarioResponse editar(
            @Parameter(description = "ID do usuário") Long id,
            EditarUsuarioRequest request);

    @Operation(summary = "Remover usuário por ID (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    void remover(@Parameter(description = "ID do usuário") Long id);

    @Operation(summary = "Consultar perfil público consolidado e reputação do vendedor")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil do vendedor retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    PerfilVendedorResponse perfilVendedor(@Parameter(description = "ID do vendedor") Long id);

    @Operation(summary = "Promover ou rebaixar permissão de administrador do usuário (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissões do usuário atualizadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou tentativa de alterar a própria permissão"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    AtualizarPermissaoUsuarioResponse atualizarPermissao(
            @Parameter(description = "ID do usuário") Long id,
            AtualizarPermissaoUsuarioRequest request);
}
