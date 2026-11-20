package br.com.guildcode.geekhub.openapi.dashboard;

import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dashboard", description = "Indicadores operacionais da plataforma")
public interface DashboardControllerOpenApi {

    @Operation(summary = "Consultar indicadores operacionais do dashboard (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Indicadores operacionais retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    ConsultarDashboardResponse consultar();
}
