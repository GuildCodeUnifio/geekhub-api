package br.com.guildcode.geekhub.controller.pedido;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.controller.pedido.dto.response.AtualizarEstadoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ItemPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.pedido.AtualizarEstadoPedidoRequestFactory;
import br.com.guildcode.geekhub.service.pedido.AtualizarEstadoPedidoService;
import br.com.guildcode.geekhub.service.pedido.CriarPedidoService;
import br.com.guildcode.geekhub.service.pedido.DetalharPedidoService;
import br.com.guildcode.geekhub.service.pedido.ListarMinhasComprasService;
import br.com.guildcode.geekhub.service.pedido.ListarMinhasVendasService;
import br.com.guildcode.geekhub.service.pedido.ListarTodosPedidosService;
import br.com.guildcode.geekhub.service.pedido.RemoverPedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static java.lang.Boolean.FALSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CriarPedidoService criarPedidoService;

    @MockitoBean
    private ListarMinhasComprasService listarMinhasComprasService;

    @MockitoBean
    private ListarMinhasVendasService listarMinhasVendasService;

    @MockitoBean
    private ListarTodosPedidosService listarTodosPedidosService;

    @MockitoBean
    private DetalharPedidoService detalharPedidoService;

    @MockitoBean
    private AtualizarEstadoPedidoService atualizarEstadoPedidoService;

    @MockitoBean
    private RemoverPedidoService removerPedidoService;

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

    @Test
    @DisplayName("Deve retornar 401 ao listar compras sem autenticação")
    void deveRetornar401AoListarComprasSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/pedidos/compras"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(listarMinhasComprasService);
    }

    @Test
    @DisplayName("Deve listar compras do comprador autenticado sem filtro e com filtro por estado")
    void deveListarComprasDoCompradorComOuSemFiltro() throws Exception {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 13, 18, 40);
        ListarCompraResponse item = ListarCompraResponse.builder()
                .id(3L)
                .valorTotal(new BigDecimal("99.80"))
                .estado(CONCLUIDO)
                .avaliado(FALSE)
                .quantidadeItens(2)
                .criadoEm(criadoEm)
                .build();

        when(listarMinhasComprasService.listar(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));
        when(listarMinhasComprasService.listar(eq(CONCLUIDO), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        // Act & Assert (sem filtro)
        mockMvc.perform(get("/pedidos/compras")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3L))
                .andExpect(jsonPath("$.content[0].quantidadeItens").value(2));

        // Act & Assert (com filtro)
        mockMvc.perform(get("/pedidos/compras")
                        .param("estado", "CONCLUIDO")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estado").value("CONCLUIDO"));

        verify(listarMinhasComprasService).listar(isNull(), any(Pageable.class));
        verify(listarMinhasComprasService).listar(eq(CONCLUIDO), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar 401 ao listar vendas sem autenticação")
    void deveRetornar401AoListarVendasSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/pedidos/vendas"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(listarMinhasVendasService);
    }

    @Test
    @DisplayName("Deve listar vendas recebidas do vendedor autenticado com filtro opcional")
    void deveListarVendasDoVendedorComFiltroOpcional() throws Exception {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 13, 18, 35);
        ListarVendaResponse item = ListarVendaResponse.builder()
                .id(2L)
                .compradorNome("Usuario GeekHub")
                .valorTotal(new BigDecimal("329.90"))
                .estado(ENVIADO)
                .criadoEm(criadoEm)
                .build();

        when(listarMinhasVendasService.listar(eq(ENVIADO), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        // Act & Assert
        mockMvc.perform(get("/pedidos/vendas")
                        .param("estado", "ENVIADO")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2L))
                .andExpect(jsonPath("$.content[0].compradorNome").value("Usuario GeekHub"))
                .andExpect(jsonPath("$.content[0].estado").value("ENVIADO"));

        verify(listarMinhasVendasService).listar(eq(ENVIADO), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar 401 ao listar todos os pedidos sem autenticação")
    void deveRetornar401AoListarTodosPedidosSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(listarTodosPedidosService);
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário comum tentar listar todos os pedidos")
    void deveRetornar403QuandoUsuarioComumListarTodosPedidos() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/pedidos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(listarTodosPedidosService);
    }

    @Test
    @DisplayName("Deve listar todos os pedidos quando usuário possuir permissão ADMIN")
    void deveListarTodosPedidosParaAdmin() throws Exception {
        // Arrange
        ListarPedidoAdminResponse item = ListarPedidoAdminResponse.builder()
                .id(1L)
                .compradorId(2L)
                .compradorNome("Usuario GeekHub")
                .compradorEmail("usuario@geekhub.com")
                .valorTotal(new BigDecimal("1850.00"))
                .estado(CRIADO)
                .avaliado(FALSE)
                .quantidadeItens(1)
                .criadoEm(LocalDateTime.of(2026, 11, 13, 18, 30))
                .build();

        when(listarTodosPedidosService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        // Act & Assert
        mockMvc.perform(get("/pedidos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].compradorEmail").value("usuario@geekhub.com"));

        verify(listarTodosPedidosService).listar(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar 401 ao detalhar pedido sem autenticação")
    void deveRetornar401AoDetalharPedidoSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/pedidos/2"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(detalharPedidoService);
    }

    @Test
    @DisplayName("Deve detalhar pedido com itens congelados e histórico cronológico")
    void deveDetalharPedidoComSucesso() throws Exception {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 13, 18, 35);
        DetalharPedidoResponse response = DetalharPedidoResponse.builder()
                .id(2L)
                .compradorId(2L)
                .compradorNome("Usuario GeekHub")
                .compradorEmail("usuario@geekhub.com")
                .itens(List.of(ItemPedidoResponse.builder()
                        .anuncioId(3L)
                        .vendedorId(3L)
                        .nomeProduto("Estátua Kakashi Hatake 18cm")
                        .imagemProduto("https://picsum.photos/200")
                        .precoUnitario(new BigDecimal("329.90"))
                        .quantidade(1)
                        .subtotal(new BigDecimal("329.90"))
                        .build()))
                .valorTotal(new BigDecimal("329.90"))
                .estado(ENVIADO)
                .historico(List.of(
                        EventoPedidoResponse.builder().estado(CRIADO).ocorridoEm(criadoEm).build(),
                        EventoPedidoResponse.builder().estado(CONFIRMADO).ocorridoEm(criadoEm.plusMinutes(1)).build(),
                        EventoPedidoResponse.builder().estado(ENVIADO).ocorridoEm(criadoEm.plusMinutes(2)).build()))
                .avaliado(FALSE)
                .criadoEm(criadoEm)
                .build();

        when(detalharPedidoService.detalhar(2L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/pedidos/2")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.itens[0].nomeProduto").value("Estátua Kakashi Hatake 18cm"))
                .andExpect(jsonPath("$.historico[0].estado").value("CRIADO"))
                .andExpect(jsonPath("$.historico[1].estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.historico[2].estado").value("ENVIADO"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando terceiro tentar detalhar pedido sem participação")
    void deveRetornar403QuandoTerceiroDetalharPedido() throws Exception {
        // Arrange
        when(detalharPedidoService.detalhar(2L))
                .thenThrow(new AccessDeniedException("Você não possui permissão para visualizar este pedido"));

        // Act & Assert
        mockMvc.perform(get("/pedidos/2")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve retornar 404 quando detalhar pedido inexistente")
    void deveRetornar404QuandoDetalharPedidoInexistente() throws Exception {
        // Arrange
        when(detalharPedidoService.detalhar(999L))
                .thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/pedidos/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Pedido não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao atualizar estado do pedido sem autenticação")
    void deveRetornar401AoAtualizarEstadoSemAutenticacao() throws Exception {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get();

        // Act & Assert
        mockMvc.perform(put("/pedidos/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(atualizarEstadoPedidoService);
    }

    @Test
    @DisplayName("Deve atualizar estado do pedido e retornar 200 OK com histórico atualizado")
    void deveAtualizarEstadoDoPedidoComSucesso() throws Exception {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 13, 18, 30);
        LocalDateTime confirmadoEm = LocalDateTime.of(2026, 11, 15, 21, 0);
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONFIRMADO);
        AtualizarEstadoPedidoResponse response = AtualizarEstadoPedidoResponse.builder()
                .id(1L)
                .estado(CONFIRMADO)
                .historico(List.of(
                        EventoPedidoResponse.builder().estado(CRIADO).ocorridoEm(criadoEm).build(),
                        EventoPedidoResponse.builder().estado(CONFIRMADO).ocorridoEm(confirmadoEm).build()))
                .avaliado(FALSE)
                .build();

        when(atualizarEstadoPedidoService.atualizar(1L, request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/pedidos/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.avaliado").value(false))
                .andExpect(jsonPath("$.historico[0].estado").value("CRIADO"))
                .andExpect(jsonPath("$.historico[1].estado").value("CONFIRMADO"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando estado na requisição for nulo ao atualizar pedido")
    void deveRetornar400QuandoEstadoNuloNaRequisicao() throws Exception {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(null);

        // Act & Assert
        mockMvc.perform(put("/pedidos/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("é obrigatório"));

        verifyNoInteractions(atualizarEstadoPedidoService);
    }

    @Test
    @DisplayName("Deve retornar 400 quando transição de estado for inválida")
    void deveRetornar400QuandoTransicaoInvalida() throws Exception {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONCLUIDO);
        when(atualizarEstadoPedidoService.atualizar(1L, request))
                .thenThrow(new NegocioException("Transição de estado inválida de CRIADO para CONCLUIDO"));

        // Act & Assert
        mockMvc.perform(put("/pedidos/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Transição de estado inválida de CRIADO para CONCLUIDO"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário sem permissão tentar atualizar estado do pedido")
    void deveRetornar403AoAtualizarEstadoSemPermissao() throws Exception {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONFIRMADO);
        when(atualizarEstadoPedidoService.atualizar(1L, request))
                .thenThrow(new AccessDeniedException("Você não possui permissão para avançar o estado deste pedido"));

        // Act & Assert
        mockMvc.perform(put("/pedidos/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar estado de pedido inexistente")
    void deveRetornar404AoAtualizarPedidoInexistente() throws Exception {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONFIRMADO);
        when(atualizarEstadoPedidoService.atualizar(999L, request))
                .thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        // Act & Assert
        mockMvc.perform(put("/pedidos/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Pedido não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao remover pedido sem autenticação")
    void deveRetornar401AoRemoverPedidoSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/pedidos/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(removerPedidoService);
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário comum tentar remover pedido")
    void deveRetornar403QuandoUsuarioComumRemoverPedido() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/pedidos/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(removerPedidoService);
    }

    @Test
    @DisplayName("Deve remover pedido sem avaliação vinculada quando usuário possuir permissão ADMIN")
    void deveRemoverPedidoComSucessoParaAdmin() throws Exception {
        // Arrange
        doNothing().when(removerPedidoService).remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/pedidos/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(removerPedidoService).remover(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 quando ADMIN tentar remover pedido inexistente")
    void deveRetornar404QuandoAdminRemoverPedidoInexistente() throws Exception {
        // Arrange
        doThrow(new RecursoNaoEncontradoException("Pedido não encontrado"))
                .when(removerPedidoService)
                .remover(999L);

        // Act & Assert
        mockMvc.perform(delete("/pedidos/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Pedido não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando ADMIN tentar remover pedido com avaliação vinculada")
    void deveRetornar409QuandoAdminRemoverPedidoComAvaliacao() throws Exception {
        // Arrange
        doThrow(new ConflitoException("Não é possível remover pedido pois possui avaliação vinculada"))
                .when(removerPedidoService)
                .remover(4L);

        // Act & Assert
        mockMvc.perform(delete("/pedidos/4")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Não é possível remover pedido pois possui avaliação vinculada"));
    }
}
