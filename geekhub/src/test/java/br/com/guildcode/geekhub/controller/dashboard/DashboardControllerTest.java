package br.com.guildcode.geekhub.controller.dashboard;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.CategoriaKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.ConsultarDashboardResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.EstadoPedidoKpiResponse;
import br.com.guildcode.geekhub.controller.dashboard.dto.response.PedidoRecenteDashboardResponse;
import br.com.guildcode.geekhub.service.dashboard.ConsultarDashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarDashboardService consultarDashboardService;

    @Test
    @DisplayName("Deve retornar 401 ao consultar dashboard sem autenticação")
    void deveRetornar401SemAutenticacao() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(consultarDashboardService);
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário comum tentar consultar o dashboard")
    void deveRetornar403QuandoUsuarioNaoForAdmin() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/dashboard")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(consultarDashboardService);
    }

    @Test
    @DisplayName("Deve retornar 200 OK com indicadores operacionais quando usuário for ADMIN")
    void deveConsultarDashboardParaAdmin() throws Exception {

        // Arrange
        ConsultarDashboardResponse response = ConsultarDashboardResponse.builder()
                .receitaTotal(new BigDecimal("2922.60"))
                .ticketMedio(new BigDecimal("584.52"))
                .totalPedidos(6L)
                .pedidosConcluidos(2L)
                .pedidosEmAndamento(3L)
                .pedidosCancelados(1L)
                .taxaConclusao(33)
                .totalAnuncios(11L)
                .anunciosDisponiveis(10L)
                .anunciosIndisponiveis(1L)
                .unidadesEmEstoque(42L)
                .totalCategoriasAtivas(4L)
                .notaMediaAvaliacoes(5.0)
                .totalAvaliacoes(2L)
                .pedidosPorEstado(List.of(
                        EstadoPedidoKpiResponse.builder()
                                .estado(CONCLUIDO)
                                .quantidade(2L)
                                .percentual(33)
                                .build()
                ))
                .anunciosPorCategoria(List.of(
                        CategoriaKpiResponse.builder()
                                .nome("Figuras colecionáveis")
                                .quantidadeAnuncios(4L)
                                .estoqueTotal(12L)
                                .percentual(36)
                                .build()
                ))
                .pedidosRecentes(List.of(
                        PedidoRecenteDashboardResponse.builder()
                                .id(10L)
                                .compradorNome("Usuario GeekHub")
                                .estado(CONCLUIDO)
                                .valorTotal(new BigDecimal("1850.00"))
                                .quantidadeItens(1)
                                .criadoEm(LocalDateTime.of(2026, 11, 20, 12, 0))
                                .build()
                ))
                .build();

        when(consultarDashboardService.consultar()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/dashboard")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receitaTotal").value(2922.6))
                .andExpect(jsonPath("$.ticketMedio").value(584.52))
                .andExpect(jsonPath("$.totalPedidos").value(6L))
                .andExpect(jsonPath("$.taxaConclusao").value(33))
                .andExpect(jsonPath("$.notaMediaAvaliacoes").value(5.0))
                .andExpect(jsonPath("$.pedidosPorEstado[0].estado").value("CONCLUIDO"))
                .andExpect(jsonPath("$.anunciosPorCategoria[0].nome").value("Figuras colecionáveis"))
                .andExpect(jsonPath("$.pedidosRecentes[0].id").value(10L));

        verify(consultarDashboardService).consultar();
    }
}
