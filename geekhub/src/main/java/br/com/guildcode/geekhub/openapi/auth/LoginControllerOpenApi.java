package br.com.guildcode.geekhub.openapi.auth;

import br.com.guildcode.geekhub.controller.auth.request.LoginRequest;
import br.com.guildcode.geekhub.controller.auth.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticação", description = "Login e geração de token JWT")
public interface LoginControllerOpenApi {

    @Operation(summary = "Autenticar usuário e retornar token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    LoginResponse login(LoginRequest loginRequest);
}
