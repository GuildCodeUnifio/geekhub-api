package br.com.guildcode.geekhub.controller.dashboard;

import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import br.com.guildcode.geekhub.openapi.dashboard.DashboardControllerOpenApi;
import br.com.guildcode.geekhub.service.dashboard.ConsultarDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/dashboard")
public class DashboardController implements DashboardControllerOpenApi {

    private final ConsultarDashboardService consultarDashboardService;

    public DashboardController(ConsultarDashboardService consultarDashboardService) {

        this.consultarDashboardService = consultarDashboardService;
    }

    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ConsultarDashboardResponse consultar() {

        return consultarDashboardService.consultar();
    }
}
