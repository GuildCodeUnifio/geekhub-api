package br.com.guildcode.geekhub.controller.pedido;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ItemPedidoResponse;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.service.pedido.CriarPedidoService;
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

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarPedidoService criarPedidoService;

    @Test
    @DisplayName("Deve retornar 401 ao tentar criar pedido sem autenticação")
    void deveRetornar401AoCriarPedidoSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/pedidos"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(criarPedidoService);
    }

    @Test
    @DisplayName("Deve criar pedido a partir do carrinho e retornar 201 Created")
    void deveCriarPedidoComSucesso() throws Exception {
        // Arrange
        LocalDateTime agora = LocalDateTime.of(2026, 11, 15, 20, 0);
        CriarPedidoResponse response = CriarPedidoResponse.builder()
                .id(5L)
                .compradorId(2L)
                .compradorNome("Usuario GeekHub")
                .compradorEmail("usuario@geekhub.com")
                .itens(List.of(ItemPedidoResponse.builder()
                        .anuncioId(2L)
                        .vendedorId(3L)
                        .nomeProduto("Mangá Colecionável — Edição Especial")
                        .imagemProduto("https://picsum.photos/200")
                        .precoUnitario(new BigDecimal("49.90"))
                        .quantidade(2)
                        .subtotal(new BigDecimal("99.80"))
                        .build()))
                .valorTotal(new BigDecimal("99.80"))
                .estado(CRIADO)
                .historico(List.of(EventoPedidoResponse.builder()
                        .estado(CRIADO)
                        .ocorridoEm(agora)
                        .build()))
                .avaliado(FALSE)
                .criadoEm(agora)
                .build();

        when(criarPedidoService.criar()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/pedidos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.compradorId").value(2L))
                .andExpect(jsonPath("$.valorTotal").value(99.80))
                .andExpect(jsonPath("$.estado").value("CRIADO"))
                .andExpect(jsonPath("$.avaliado").value(false))
                .andExpect(jsonPath("$.itens[0].anuncioId").value(2L))
                .andExpect(jsonPath("$.historico[0].estado").value("CRIADO"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando carrinho estiver vazio")
    void deveRetornar400QuandoCarrinhoEstiverVazio() throws Exception {
        // Arrange
        when(criarPedidoService.criar())
                .thenThrow(new NegocioException("O carrinho de compras está vazio"));

        // Act & Assert
        mockMvc.perform(post("/pedidos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("O carrinho de compras está vazio"));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando algum anúncio estiver indisponível ou sem estoque")
    void deveRetornar409QuandoHouverConflitoDeEstoque() throws Exception {
        // Arrange
        when(criarPedidoService.criar())
                .thenThrow(new ConflitoException("Estoque insuficiente para o anúncio 'Mangá Colecionável — Edição Especial'"));

        // Act & Assert
        mockMvc.perform(post("/pedidos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro")
                        .value("Estoque insuficiente para o anúncio 'Mangá Colecionável — Edição Especial'"));
    }
}
